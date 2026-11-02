using System.Security.Cryptography;
using System.Text;
using Microsoft.Data.SqlClient;

public sealed class BankRepository(string connectionString)
{
    private const string EmployeeCode = "0001";
    private SqlConnection Open() => new(connectionString);

    public async Task<bool> Login(string user, string password)
    {
        await using var connection = Open();
        await connection.OpenAsync();
        await using var command = new SqlCommand(
            "SELECT TOP (1) vch_emplclave, vch_emplestado FROM dbo.Usuario WHERE vch_emplusuario=@user", connection);
        command.Parameters.AddWithValue("@user", user);
        await using var reader = await command.ExecuteReaderAsync();
        if (!await reader.ReadAsync() || reader.GetString(1) != "ACTIVO") return false;

        var stored = reader.GetString(0);
        var hash = Convert.ToHexString(SHA1.HashData(Encoding.UTF8.GetBytes(password))).ToLowerInvariant();
        return stored == password || stored.Equals(hash, StringComparison.OrdinalIgnoreCase);
    }

    public async Task<List<AccountDto>> Accounts(string? code = null)
    {
        const string sql = """
            SELECT c.chr_cuencodigo, c.chr_monecodigo, c.dec_cuensaldo, c.vch_cuenestado,
                   CONCAT(cl.vch_cliepaterno, ' ', cl.vch_cliematerno, ', ', cl.vch_clienombre),
                   cl.vch_clieemail, cl.vch_clietelefono
            FROM dbo.Cuenta c
            JOIN dbo.Cliente cl ON cl.chr_cliecodigo=c.chr_cliecodigo
            WHERE c.vch_cuenestado='ACTIVO'
              AND (@code IS NULL OR c.chr_cuencodigo=@code)
            ORDER BY c.chr_cuencodigo
            """;
        await using var connection = Open();
        await connection.OpenAsync();
        await using var command = new SqlCommand(sql, connection);
        command.Parameters.Add("@code", System.Data.SqlDbType.Char, 8).Value =
            string.IsNullOrWhiteSpace(code) ? DBNull.Value : code;
        await using var reader = await command.ExecuteReaderAsync();
        var accounts = new List<AccountDto>();
        while (await reader.ReadAsync())
            accounts.Add(new(reader.GetString(0).Trim(), reader.GetString(1).Trim(), reader.GetDecimal(2),
                reader.GetString(3), reader.GetString(4), reader.IsDBNull(5) ? null : reader.GetString(5),
                reader.IsDBNull(6) ? null : reader.GetString(6)));
        return accounts;
    }

    public async Task<List<MovementDto>> Movements(string? account)
    {
        const string sql = """
            SELECT m.chr_cuencodigo, m.int_movinumero, m.id_movimiento, m.dtt_movifecha,
                   t.vch_tipodescripcion, t.vch_tipoaccion, m.dec_moviimporte,
                   m.chr_cuenreferencia,
                   CONCAT(cl.vch_cliepaterno, ' ', cl.vch_cliematerno, ', ', cl.vch_clienombre)
            FROM dbo.Movimiento m
            JOIN dbo.TipoMovimiento t ON t.chr_tipocodigo=m.chr_tipocodigo
            JOIN dbo.Cuenta c ON c.chr_cuencodigo=m.chr_cuencodigo
            JOIN dbo.Cliente cl ON cl.chr_cliecodigo=c.chr_cliecodigo
            WHERE (@account IS NULL OR m.chr_cuencodigo=@account)
            ORDER BY m.dtt_movifecha, m.id_movimiento
            """;
        await using var connection = Open();
        await connection.OpenAsync();
        await using var command = new SqlCommand(sql, connection);
        command.Parameters.Add("@account", System.Data.SqlDbType.Char, 8).Value =
            string.IsNullOrWhiteSpace(account) ? DBNull.Value : account;
        await using var reader = await command.ExecuteReaderAsync();
        var movements = new List<MovementDto>();
        var balances = new Dictionary<string, decimal>(StringComparer.OrdinalIgnoreCase);
        while (await reader.ReadAsync())
        {
            var code = reader.GetString(0).Trim();
            var amount = reader.GetDecimal(6);
            var incoming = reader.GetString(5) == "INGRESO";
            balances[code] = balances.GetValueOrDefault(code) + (incoming ? amount : -amount);
            movements.Add(new(code, reader.GetInt32(1), reader.GetInt64(2),
                reader.GetDateTime(3).ToString("yyyy-MM-dd"),
                reader.GetString(4) + (incoming ? " Entrada" : " Salida"), amount,
                reader.IsDBNull(7) ? null : reader.GetString(7).Trim(), balances[code], reader.GetString(8)));
        }
        return movements.OrderByDescending(item => item.Fecha, StringComparer.Ordinal)
            .ThenByDescending(item => item.OrdenCreacion).ToList();
    }

    public async Task<string?> Register(MovementRequest request)
    {
        var type = request.Tipo.Trim().ToUpperInvariant();
        if (type is not ("DEPOSITO" or "RETIRO" or "TRANSFERENCIA")) return "Tipo de movimiento no válido.";
        if (request.Importe <= 0) return "El importe debe ser mayor a cero.";
        if (string.IsNullOrWhiteSpace(request.CuentaOrigen)) return "Selecciona una cuenta de origen.";
        if (type == "TRANSFERENCIA" && (string.IsNullOrWhiteSpace(request.CuentaDestino)
            || request.CuentaDestino == request.CuentaOrigen)) return "Cuenta destino inválida.";

        await using var connection = Open();
        await connection.OpenAsync();
        await using var transaction = (SqlTransaction)await connection.BeginTransactionAsync();
        try
        {
            await Move(connection, transaction, request.CuentaOrigen, request.Importe,
                type == "DEPOSITO", type, request.CuentaDestino);
            if (type == "TRANSFERENCIA")
                await Move(connection, transaction, request.CuentaDestino!, request.Importe,
                    true, type, request.CuentaOrigen);
            await transaction.CommitAsync();
            return null;
        }
        catch (Exception exception)
        {
            await transaction.RollbackAsync();
            return exception.Message;
        }
    }

    private static async Task Move(SqlConnection connection, SqlTransaction transaction, string account,
        decimal amount, bool incoming, string type, string? reference)
    {
        await using var select = new SqlCommand(
            "SELECT dec_cuensaldo, int_cuencontmov, vch_cuenestado FROM dbo.Cuenta WITH (UPDLOCK, ROWLOCK, HOLDLOCK) WHERE chr_cuencodigo=@code",
            connection, transaction);
        select.Parameters.AddWithValue("@code", account);
        await using var reader = await select.ExecuteReaderAsync();
        if (!await reader.ReadAsync()) throw new InvalidOperationException("Cuenta no existe: " + account);
        var balance = reader.GetDecimal(0);
        var count = reader.GetInt32(1);
        var active = reader.GetString(2) == "ACTIVO";
        await reader.CloseAsync();
        if (!active) throw new InvalidOperationException("Cuenta inactiva.");
        if (!incoming && balance < amount) throw new InvalidOperationException("Saldo insuficiente.");

        var movementCode = type switch
        {
            "DEPOSITO" => "003",
            "RETIRO" => "004",
            _ => incoming ? "008" : "009"
        };
        await using (var update = new SqlCommand(
            "UPDATE dbo.Cuenta SET dec_cuensaldo=@balance, int_cuencontmov=@count WHERE chr_cuencodigo=@code",
            connection, transaction))
        {
            update.Parameters.AddWithValue("@balance", balance + (incoming ? amount : -amount));
            update.Parameters.AddWithValue("@count", count + 1);
            update.Parameters.AddWithValue("@code", account);
            await update.ExecuteNonQueryAsync();
        }

        await using var insert = new SqlCommand("""
            INSERT INTO dbo.Movimiento
                (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia)
            SELECT @code, COALESCE(MAX(int_movinumero),0)+1, CONVERT(date, GETDATE()), @employee, @type, @amount, @reference
            FROM dbo.Movimiento WHERE chr_cuencodigo=@code
            """, connection, transaction);
        insert.Parameters.AddWithValue("@code", account);
        insert.Parameters.AddWithValue("@employee", EmployeeCode);
        insert.Parameters.AddWithValue("@type", movementCode);
        insert.Parameters.AddWithValue("@amount", amount);
        insert.Parameters.AddWithValue("@reference", (object?)reference ?? DBNull.Value);
        await insert.ExecuteNonQueryAsync();
    }
}

public record AccountDto(string Codigo, string Moneda, decimal Saldo, string Estado, string NombreCliente,
    string? EmailCliente, string? TelefonoCliente);
public record MovementDto(string CodigoCuenta, int Numero, long OrdenCreacion, string Fecha, string Tipo,
    decimal Importe, string? Referencia, decimal SaldoActual, string NombreCliente);
public record MovementRequest(string CuentaOrigen, string? CuentaDestino, string Tipo, decimal Importe);
