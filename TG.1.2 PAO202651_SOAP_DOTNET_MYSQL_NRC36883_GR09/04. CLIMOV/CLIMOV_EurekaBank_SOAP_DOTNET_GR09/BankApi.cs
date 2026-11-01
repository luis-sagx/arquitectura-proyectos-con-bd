using System.Globalization;
using System.Xml.Linq;

namespace CLIMOV_EurekaBank_SOAP_DOTNET_GR09;

internal static class BankApi
{
    public static async Task<bool> Login(string user, string password)
    {
        var response = await SoapTransport.CallAsync("LoginService.svc", SoapTransport.LoginNamespace,
            "ILoginService", "Login", SoapTransport.Argument(SoapTransport.LoginNamespace, "usuario", user),
            SoapTransport.Argument(SoapTransport.LoginNamespace, "clave", password));
        return bool.TryParse(SoapTransport.Result(response, "LoginResult").Value, out var success) && success;
    }

    public static async Task<List<AccountDto>> Accounts()
    {
        var response = await SoapTransport.CallAsync("MovimientosService.svc", SoapTransport.MovementsNamespace,
            "IMovimientosService", "GetDatosCuentas");
        return SoapTransport.Items(response, "GetDatosCuentasResult", "CuentaData").Select(item => new AccountDto
        {
            Codigo = SoapTransport.Text(item, "Codigo"), Moneda = SoapTransport.Text(item, "Moneda"),
            Saldo = SoapTransport.Decimal(item, "Saldo"), Estado = SoapTransport.Text(item, "Estado"),
            NombreCliente = SoapTransport.Text(item, "NombreCliente"), EmailCliente = SoapTransport.Text(item, "EmailCliente"),
            TelefonoCliente = SoapTransport.Text(item, "TelefonoCliente")
        }).ToList();
    }

    public static async Task<List<MovementDto>> Movements(string? account = null)
    {
        var response = await SoapTransport.CallAsync("MovimientosService.svc", SoapTransport.MovementsNamespace,
            "IMovimientosService", "GetMovimientos", SoapTransport.Argument(SoapTransport.MovementsNamespace, "cuenta", account ?? ""));
        return SoapTransport.Items(response, "GetMovimientosResult", "MovimientoData").Select(item => new MovementDto
        {
            CodigoCuenta = SoapTransport.Text(item, "CodigoCuenta"),
            Numero = int.TryParse(SoapTransport.Text(item, "Numero"), out var number) ? number : 0,
            OrdenCreacion = long.TryParse(SoapTransport.Text(item, "OrdenCreacion"), out var order) ? order : 0,
            Fecha = SoapTransport.Text(item, "Fecha"), Tipo = SoapTransport.Text(item, "Tipo"),
            Importe = SoapTransport.Decimal(item, "Importe"), Referencia = SoapTransport.Text(item, "Referencia"),
            SaldoActual = SoapTransport.Decimal(item, "SaldoActual"), NombreCliente = SoapTransport.Text(item, "NombreCliente")
        }).ToList();
    }

    public static async Task<List<string>> MovementTypes()
    {
        var response = await SoapTransport.CallAsync("MovimientosService.svc", SoapTransport.MovementsNamespace,
            "IMovimientosService", "GetTiposMovimiento");
        return SoapTransport.Items(response, "GetTiposMovimientoResult", "string").Select(item => item.Value).ToList();
    }

    public static async Task<string?> CreateMovement(string origin, string? destination, string type, decimal amount)
    {
        var response = await SoapTransport.CallAsync("MovimientosService.svc", SoapTransport.MovementsNamespace,
            "IMovimientosService", "ProcesarMovimiento",
            SoapTransport.Argument(SoapTransport.MovementsNamespace, "tipo", type),
            SoapTransport.Argument(SoapTransport.MovementsNamespace, "cuentaOrigen", origin),
            SoapTransport.Argument(SoapTransport.MovementsNamespace, "cuentaDestino", destination ?? ""),
            SoapTransport.Argument(SoapTransport.MovementsNamespace, "importe", amount.ToString(CultureInfo.InvariantCulture)));
        var result = SoapTransport.Result(response, "ProcesarMovimientoResult").Elements().FirstOrDefault();
        if (result is null) return "El servicio no devolvió el resultado de la operación.";
        return SoapTransport.Text(result, "Codigo") == "1" ? null : SoapTransport.Text(result, "Mensaje");
    }
}

internal sealed class AccountDto
{
    public string Codigo { get; set; } = "";
    public string Moneda { get; set; } = "";
    public decimal Saldo { get; set; }
    public string Estado { get; set; } = "";
    public string NombreCliente { get; set; } = "";
    public string? EmailCliente { get; set; }
    public string? TelefonoCliente { get; set; }
    public override string ToString() => $"{Codigo} · {NombreCliente} · {Saldo:N2} {Moneda}";
}

internal sealed class MovementDto
{
    public string CodigoCuenta { get; set; } = "";
    public int Numero { get; set; }
    public long OrdenCreacion { get; set; }
    public string Fecha { get; set; } = "";
    public string Tipo { get; set; } = "";
    public decimal Importe { get; set; }
    public string? Referencia { get; set; }
    public decimal SaldoActual { get; set; }
    public string NombreCliente { get; set; } = "";
}
