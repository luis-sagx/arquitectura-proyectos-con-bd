using System.Security.Cryptography;
using System.Text;
using MySqlConnector;
using WS_EuBank_SOAPDOTNET_GR09.Contracts;

namespace WS_EuBank_SOAPDOTNET_GR09;

public sealed class BankRepository(string connectionString)
{
    private const string EmployeeCode = "0001";
    private MySqlConnection OpenConnection() => new(connectionString);

    public async Task<bool> LoginAsync(string user, string password, CancellationToken cancellationToken)
    {
        await using var connection = OpenConnection();
        await connection.OpenAsync(cancellationToken);
        await using var command = new MySqlCommand(
            "SELECT vch_emplclave, vch_emplestado FROM Usuario WHERE vch_emplusuario=@user LIMIT 1", connection);
        command.Parameters.AddWithValue("@user", user);
        await using var reader = await command.ExecuteReaderAsync(cancellationToken);
        if (!await reader.ReadAsync(cancellationToken) || reader.GetString(1) != "ACTIVO") return false;

        var stored = reader.GetString(0);
        var hash = Convert.ToHexString(SHA1.HashData(Encoding.UTF8.GetBytes(password))).ToLowerInvariant();
        return stored == password || stored.Equals(hash, StringComparison.OrdinalIgnoreCase);
    }

    public async Task<List<CuentaData>> GetAccountsAsync(CancellationToken cancellationToken)
    {
        const string sql = """
            SELECT c.chr_cuencodigo, c.chr_monecodigo, c.dec_cuensaldo, c.vch_cuenestado,
                   CONCAT(cl.vch_cliepaterno, ' ', cl.vch_cliematerno, ', ', cl.vch_clienombre),
                   cl.vch_clieemail, cl.vch_clietelefono
            FROM Cuenta c JOIN Cliente cl ON cl.chr_cliecodigo=c.chr_cliecodigo
            WHERE c.vch_cuenestado='ACTIVO'
            ORDER BY c.chr_cuencodigo
            """;
        await using var connection = OpenConnection();
        await connection.OpenAsync(cancellationToken);
        await using var command = new MySqlCommand(sql, connection);
        await using var reader = await command.ExecuteReaderAsync(cancellationToken);
        var accounts = new List<CuentaData>();
        while (await reader.ReadAsync(cancellationToken)) accounts.Add(ReadAccount(reader));
        return accounts;
    }

    public async Task<CuentaData?> GetAccountAsync(string code, CancellationToken cancellationToken)
    {
        const string sql = """
            SELECT c.chr_cuencodigo, c.chr_monecodigo, c.dec_cuensaldo, c.vch_cuenestado,
                   CONCAT(cl.vch_cliepaterno, ' ', cl.vch_cliematerno, ', ', cl.vch_clienombre),
                   cl.vch_clieemail, cl.vch_clietelefono
            FROM Cuenta c JOIN Cliente cl ON cl.chr_cliecodigo=c.chr_cliecodigo
            WHERE c.chr_cuencodigo=@code
            LIMIT 1
            """;
        await using var connection = OpenConnection();
        await connection.OpenAsync(cancellationToken);
        await using var command = new MySqlCommand(sql, connection);
        command.Parameters.AddWithValue("@code", code);
        await using var reader = await command.ExecuteReaderAsync(cancellationToken);
        return await reader.ReadAsync(cancellationToken) ? ReadAccount(reader) : null;
    }

    private static CuentaData ReadAccount(MySqlDataReader reader) => new()
    {
        Codigo = reader.GetString(0).Trim(),
        Moneda = reader.GetString(1).Trim(),
        Saldo = reader.GetDecimal(2),
        Estado = reader.GetString(3),
        NombreCliente = reader.GetString(4),
        EmailCliente = reader.IsDBNull(5) ? null : reader.GetString(5),
        TelefonoCliente = reader.IsDBNull(6) ? null : reader.GetString(6)
    };

    public async Task<List<MovimientoData>> GetMovementsAsync(string? account, CancellationToken cancellationToken)
    {
        var sql = """
            SELECT m.chr_cuencodigo, m.int_movinumero, m.id_movimiento, m.dtt_movifecha,
                   t.vch_tipodescripcion, t.vch_tipoaccion, m.dec_moviimporte,
                   m.chr_cuenreferencia, CONCAT(cl.vch_cliepaterno, ' ', cl.vch_cliematerno, ', ', cl.vch_clienombre)
            FROM Movimiento m
            JOIN TipoMovimiento t ON t.chr_tipocodigo=m.chr_tipocodigo
            JOIN Cuenta c ON c.chr_cuencodigo=m.chr_cuencodigo
            JOIN Cliente cl ON cl.chr_cliecodigo=c.chr_cliecodigo
            """;
        if (!string.IsNullOrWhiteSpace(account)) sql += " WHERE m.chr_cuencodigo=@account";
        sql += " ORDER BY m.dtt_movifecha, m.id_movimiento";

        await using var connection = OpenConnection();
        await connection.OpenAsync(cancellationToken);
        await using var command = new MySqlCommand(sql, connection);
        if (!string.IsNullOrWhiteSpace(account)) command.Parameters.AddWithValue("@account", account);
        await using var reader = await command.ExecuteReaderAsync(cancellationToken);
        var movements = new List<MovimientoData>();
        var balances = new Dictionary<string, decimal>(StringComparer.OrdinalIgnoreCase);
        while (await reader.ReadAsync(cancellationToken))
        {
            var code = reader.GetString(0).Trim();
            var incoming = reader.GetString(5) == "INGRESO";
            var amount = reader.GetDecimal(6);
            balances[code] = balances.GetValueOrDefault(code) + (incoming ? amount : -amount);
            movements.Add(new MovimientoData
            {
                CodigoCuenta = code,
                Numero = reader.GetInt32(1),
                OrdenCreacion = reader.GetInt64(2),
                Fecha = reader.GetDateTime(3).ToString("yyyy-MM-dd", System.Globalization.CultureInfo.InvariantCulture),
                Tipo = reader.GetString(4) + (incoming ? " Entrada" : " Salida"),
                Importe = amount,
                Referencia = reader.IsDBNull(7) ? null : reader.GetString(7)?.Trim(),
                SaldoActual = balances[code],
                NombreCliente = reader.GetString(8)
            });
        }
        return movements.OrderByDescending(m => m.Fecha, StringComparer.Ordinal)
            .ThenByDescending(m => m.OrdenCreacion).ToList();
    }

    public async Task<string?> RegisterMovementAsync(string type, string origin, string? destination, decimal amount, CancellationToken cancellationToken)
    {
        type = type.Trim().ToUpperInvariant();
        if (type is not ("DEPOSITO" or "RETIRO" or "TRANSFERENCIA")) return "Tipo de movimiento no válido.";
        if (amount <= 0) return "El importe debe ser mayor a cero.";
        if (string.IsNullOrWhiteSpace(origin)) return "Selecciona una cuenta de origen.";
        if (type == "TRANSFERENCIA" && (string.IsNullOrWhiteSpace(destination) || destination == origin))
            return "La cuenta de destino no es válida.";

        await using var connection = OpenConnection();
        await connection.OpenAsync(cancellationToken);
        await using var transaction = await connection.BeginTransactionAsync(cancellationToken);
        try
        {
            await MoveAsync(connection, transaction, origin, amount, type == "DEPOSITO", type, destination, cancellationToken);
            if (type == "TRANSFERENCIA")
                await MoveAsync(connection, transaction, destination!, amount, true, type, origin, cancellationToken);
            await transaction.CommitAsync(cancellationToken);
            return null;
        }
        catch (Exception ex)
        {
            await transaction.RollbackAsync(cancellationToken);
            return ex.Message;
        }
    }

    private static async Task MoveAsync(MySqlConnection connection, MySqlTransaction transaction, string account,
        decimal amount, bool incoming, string type, string? reference, CancellationToken cancellationToken)
    {
        await using var select = new MySqlCommand(
            "SELECT dec_cuensaldo, int_cuencontmov, vch_cuenestado FROM Cuenta WHERE chr_cuencodigo=@code FOR UPDATE",
            connection, transaction);
        select.Parameters.AddWithValue("@code", account);
        await using var reader = await select.ExecuteReaderAsync(cancellationToken);
        if (!await reader.ReadAsync(cancellationToken)) throw new InvalidOperationException($"La cuenta {account} no existe.");
        var balance = reader.GetDecimal(0);
        var count = reader.GetInt32(1);
        var active = reader.GetString(2) == "ACTIVO";
        await reader.CloseAsync();
        if (!active) throw new InvalidOperationException($"La cuenta {account} está inactiva.");
        if (!incoming && balance < amount) throw new InvalidOperationException("Saldo insuficiente.");

        var movementCode = type switch
        {
            "DEPOSITO" => "003",
            "RETIRO" => "004",
            _ => incoming ? "008" : "009"
        };
        await using (var update = new MySqlCommand(
            "UPDATE Cuenta SET dec_cuensaldo=@balance, int_cuencontmov=@count WHERE chr_cuencodigo=@code",
            connection, transaction))
        {
            update.Parameters.AddWithValue("@balance", balance + (incoming ? amount : -amount));
            update.Parameters.AddWithValue("@count", count + 1);
            update.Parameters.AddWithValue("@code", account);
            await update.ExecuteNonQueryAsync(cancellationToken);
        }
        await using var insert = new MySqlCommand("""
            INSERT INTO Movimiento(chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia)
            SELECT @code, COALESCE(MAX(int_movinumero),0)+1, CURDATE(), @employee, @type, @amount, @reference
            FROM Movimiento WHERE chr_cuencodigo=@code
            """, connection, transaction);
        insert.Parameters.AddWithValue("@code", account);
        insert.Parameters.AddWithValue("@employee", EmployeeCode);
        insert.Parameters.AddWithValue("@type", movementCode);
        insert.Parameters.AddWithValue("@amount", amount);
        insert.Parameters.AddWithValue("@reference", (object?)reference ?? DBNull.Value);
        await insert.ExecuteNonQueryAsync(cancellationToken);
    }
}
