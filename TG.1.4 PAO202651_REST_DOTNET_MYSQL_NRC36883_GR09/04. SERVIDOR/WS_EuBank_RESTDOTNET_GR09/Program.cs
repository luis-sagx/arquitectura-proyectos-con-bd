using MySqlConnector;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
builder.Services.AddSingleton(new BankRepository(Environment.GetEnvironmentVariable("DB_CONNECTION") ?? "Server=localhost;Port=3306;Database=eurekabank;User ID=monster;Password=monster9;SslMode=None;Allow User Variables=True"));
builder.Services.AddCors(o => o.AddDefaultPolicy(p => p.AllowAnyOrigin().AllowAnyHeader().AllowAnyMethod()));
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseAuthorization();
app.UseCors();

app.MapControllers();

app.Run();

public partial class Program { }

public sealed class BankRepository(string connectionString)
{
    private MySqlConnection Open() => new(connectionString);
    public async Task<bool> Login(string user, string password)
    {
        await using var cn=Open(); await cn.OpenAsync();
        await using var cmd=new MySqlCommand("SELECT vch_emplclave,vch_emplestado FROM Usuario WHERE vch_emplusuario=@u LIMIT 1",cn); cmd.Parameters.AddWithValue("@u",user);
        await using var r=await cmd.ExecuteReaderAsync(); if(!await r.ReadAsync()||r.GetString(1)!="ACTIVO") return false;
        var stored=r.GetString(0); var hash=Convert.ToHexString(System.Security.Cryptography.SHA1.HashData(System.Text.Encoding.UTF8.GetBytes(password))).ToLowerInvariant();
        return stored==password || stored.Equals(hash,StringComparison.OrdinalIgnoreCase);
    }
    public async Task<List<AccountDto>> Accounts(string? code=null)
    {
        await using var cn=Open(); await cn.OpenAsync(); var sql="SELECT c.chr_cuencodigo,c.chr_monecodigo,c.dec_cuensaldo,c.vch_cuenestado,CONCAT(cl.vch_cliepaterno,' ',cl.vch_cliematerno,', ',cl.vch_clienombre),cl.vch_clieemail,cl.vch_clietelefono FROM Cuenta c JOIN Cliente cl ON cl.chr_cliecodigo=c.chr_cliecodigo WHERE c.vch_cuenestado='ACTIVO'";
        if(!string.IsNullOrWhiteSpace(code))sql+=" AND c.chr_cuencodigo=@c"; sql+=" ORDER BY c.chr_cuencodigo";
        await using var cmd=new MySqlCommand(sql,cn); if(!string.IsNullOrWhiteSpace(code))cmd.Parameters.AddWithValue("@c",code); await using var r=await cmd.ExecuteReaderAsync(); var list=new List<AccountDto>();
        while(await r.ReadAsync())list.Add(new(r.GetString(0).Trim(),r.GetString(1).Trim(),r.GetDecimal(2),r.GetString(3),r.GetString(4),r.IsDBNull(5)?null:r.GetString(5),r.IsDBNull(6)?null:r.GetString(6))); return list;
    }
    public async Task<List<MovementDto>> Movements(string? account)
    {
        await using var cn=Open();await cn.OpenAsync();var sql="SELECT m.chr_cuencodigo,m.int_movinumero,m.id_movimiento,m.dtt_movifecha,t.vch_tipodescripcion,t.vch_tipoaccion,m.dec_moviimporte,m.chr_cuenreferencia,CONCAT(cl.vch_cliepaterno,' ',cl.vch_cliematerno,', ',cl.vch_clienombre) FROM Movimiento m JOIN TipoMovimiento t ON t.chr_tipocodigo=m.chr_tipocodigo JOIN Cuenta c ON c.chr_cuencodigo=m.chr_cuencodigo JOIN Cliente cl ON cl.chr_cliecodigo=c.chr_cliecodigo";
        if(!string.IsNullOrWhiteSpace(account))sql+=" WHERE m.chr_cuencodigo=@c";sql+=" ORDER BY m.dtt_movifecha,m.int_movinumero";
        await using var cmd=new MySqlCommand(sql,cn);if(!string.IsNullOrWhiteSpace(account))cmd.Parameters.AddWithValue("@c",account);await using var r=await cmd.ExecuteReaderAsync();var list=new List<MovementDto>();var balances=new Dictionary<string,decimal>();
        while(await r.ReadAsync()){var c=r.GetString(0).Trim();var amount=r.GetDecimal(6);var incoming=r.GetString(5)=="INGRESO";balances[c]=balances.GetValueOrDefault(c)+(incoming?amount:-amount);list.Add(new(c,r.GetInt32(1),r.GetInt64(2),r.GetDateTime(3).ToString("yyyy-MM-dd"),r.GetString(4)+(incoming?" Entrada":" Salida"),amount,r.IsDBNull(7)?null:r.GetString(7)?.Trim(),balances[c],r.GetString(8)));}
        return list.OrderByDescending(x=>x.Fecha).ThenByDescending(x=>x.OrdenCreacion).ToList();
    }
    public async Task<string?> Register(MovementRequest request)
    {
        if (request.Tipo is not ("DEPOSITO" or "RETIRO" or "TRANSFERENCIA")) return "Tipo de movimiento no válido.";
        if(request.Importe<=0)return "El importe debe ser mayor a cero."; if(request.Tipo=="TRANSFERENCIA"&&(string.IsNullOrWhiteSpace(request.CuentaDestino)||request.CuentaDestino==request.CuentaOrigen))return "Cuenta destino inválida.";
        await using var cn=Open();await cn.OpenAsync();await using var tx=await cn.BeginTransactionAsync();
        try{await Move(cn,tx,request.CuentaOrigen,request.Importe,request.Tipo=="DEPOSITO",request.Tipo,request.CuentaDestino);if(request.Tipo=="TRANSFERENCIA")await Move(cn,tx,request.CuentaDestino!,request.Importe,true,request.Tipo,request.CuentaOrigen);await tx.CommitAsync();return null;}catch(Exception ex){await tx.RollbackAsync();return ex.Message;}
    }
    private static async Task Move(MySqlConnection cn,MySqlTransaction tx,string account,decimal amount,bool income,string type,string? reference)
    {
        await using var cmd=new MySqlCommand("SELECT dec_cuensaldo,int_cuencontmov,vch_cuenestado FROM Cuenta WHERE chr_cuencodigo=@c FOR UPDATE",cn,tx);cmd.Parameters.AddWithValue("@c",account);await using var r=await cmd.ExecuteReaderAsync();if(!await r.ReadAsync())throw new Exception("Cuenta no existe: "+account);var balance=r.GetDecimal(0);var count=r.GetInt32(1);var active=r.GetString(2)=="ACTIVO";await r.CloseAsync();if(!active)throw new Exception("Cuenta inactiva");if(!income&&balance<amount)throw new Exception("Saldo insuficiente");
        var code=type=="DEPOSITO"?"003":type=="RETIRO"?"004":income?"008":"009";await using var u=new MySqlCommand("UPDATE Cuenta SET dec_cuensaldo=@s,int_cuencontmov=@n WHERE chr_cuencodigo=@c",cn,tx);u.Parameters.AddWithValue("@s",balance+(income?amount:-amount));u.Parameters.AddWithValue("@n",count+1);u.Parameters.AddWithValue("@c",account);await u.ExecuteNonQueryAsync();
        await using var i=new MySqlCommand("INSERT INTO Movimiento(chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) SELECT @c,COALESCE(MAX(int_movinumero),0)+1,CURDATE(),'0001',@t,@a,@r FROM Movimiento WHERE chr_cuencodigo=@c",cn,tx);i.Parameters.AddWithValue("@c",account);i.Parameters.AddWithValue("@t",code);i.Parameters.AddWithValue("@a",amount);i.Parameters.AddWithValue("@r",(object?)reference??DBNull.Value);await i.ExecuteNonQueryAsync();
    }
}
public record AccountDto(string Codigo,string Moneda,decimal Saldo,string Estado,string NombreCliente,string? EmailCliente,string? TelefonoCliente);
public record MovementDto(string CodigoCuenta,int Numero,long OrdenCreacion,string Fecha,string Tipo,decimal Importe,string? Referencia,decimal SaldoActual,string NombreCliente);
public record MovementRequest(string CuentaOrigen,string? CuentaDestino,string Tipo,decimal Importe);
