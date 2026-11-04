using System.Globalization;
using System.Net.Http.Headers;
using System.Text;
using System.Xml.Linq;
using CLICON_EuBank_SOAP_DOTNET_GR09.Models;

namespace CLICON_EuBank_SOAP_DOTNET_GR09;

internal static class SoapBankClient
{
    private const string Root = "http://localhost:9091/Services/";
    private const string LoginNs = "http://monster.edu.ec/eurekabank/login";
    private const string MovementsNs = "http://monster.edu.ec/eurekabank/movimientos";
    private const string SoapNs = "http://schemas.xmlsoap.org/soap/envelope/";
    private static readonly HttpClient Http = new() { Timeout = TimeSpan.FromSeconds(20) };

    public static async Task<bool> LoginAsync(string user, string password)
    {
        var response = await CallAsync("LoginService.svc", LoginNs, "ILoginService", "Login",
            Arg(LoginNs, "usuario", user), Arg(LoginNs, "clave", password));
        return bool.TryParse(Result(response, "LoginResult").Value, out var success) && success;
    }

    public static async Task<List<Account>> AccountsAsync()
    {
        var response = await CallAsync("MovimientosService.svc", MovementsNs, "IMovimientosService", "GetDatosCuentas");
        return ResultItems(response, "GetDatosCuentasResult", "CuentaData").Select(item => new Account
        {
            codigo = Text(item, "Codigo"), nombreCliente = Text(item, "NombreCliente"),
            emailCliente = Text(item, "EmailCliente"), telefonoCliente = Text(item, "TelefonoCliente"),
            moneda = Text(item, "Moneda"), saldo = Decimal(item, "Saldo"), estado = Text(item, "Estado")
        }).ToList();
    }

    public static async Task<List<Movement>> MovementsAsync(string? account)
    {
        var response = await CallAsync("MovimientosService.svc", MovementsNs, "IMovimientosService", "GetMovimientos",
            Arg(MovementsNs, "cuenta", account ?? ""));
        return ResultItems(response, "GetMovimientosResult", "MovimientoData").Select(item => new Movement
        {
            numero = int.TryParse(Text(item, "Numero"), out var number) ? number : 0,
            ordenCreacion = long.TryParse(Text(item, "OrdenCreacion"), out var order) ? order : 0,
            fecha = DateTime.Parse(Text(item, "Fecha"), CultureInfo.InvariantCulture), tipo = Text(item, "Tipo"),
            importe = Decimal(item, "Importe"), referencia = Text(item, "Referencia"),
            codigoCuenta = Text(item, "CodigoCuenta"), nombreCliente = Text(item, "NombreCliente"), saldoActual = Decimal(item, "SaldoActual")
        }).ToList();
    }

    public static async Task<List<string>> MovementTypesAsync()
    {
        var response = await CallAsync("MovimientosService.svc", MovementsNs, "IMovimientosService", "GetTiposMovimiento");
        return ResultItems(response, "GetTiposMovimientoResult", "string").Select(item => item.Value).ToList();
    }

    public static async Task<string?> ProcessMovementAsync(MovementRequest request)
    {
        var response = await CallAsync("MovimientosService.svc", MovementsNs, "IMovimientosService", "ProcesarMovimiento",
            Arg(MovementsNs, "tipo", request.tipo), Arg(MovementsNs, "cuentaOrigen", request.cuentaOrigen),
            Arg(MovementsNs, "cuentaDestino", request.cuentaDestino ?? ""), Arg(MovementsNs, "importe", request.importe.ToString(CultureInfo.InvariantCulture)));
        var result = Result(response, "ProcesarMovimientoResult").Elements().FirstOrDefault();
        return result is null || Int(result, "Codigo") != 1 ? Text(result ?? response, "Mensaje") : null;
    }

    private static XElement Arg(string ns, string name, string value) => new(XName.Get(name, ns), value);

    private static async Task<XElement> CallAsync(string service, string ns, string contract, string operation, params XElement[] arguments)
    {
        XNamespace soap = SoapNs;
        var body = new XElement(soap + "Envelope", new XAttribute(XNamespace.Xmlns + "soap", soap),
            new XElement(soap + "Body", new XElement(XName.Get(operation, ns), arguments)));
        using var request = new HttpRequestMessage(HttpMethod.Post, Root + service)
        {
            Content = new StringContent(body.ToString(SaveOptions.DisableFormatting), Encoding.UTF8, "text/xml")
        };
        request.Headers.Add("SOAPAction", $"\"{ns}/{contract}/{operation}\"");
        using var response = await Http.SendAsync(request);
        var payload = await response.Content.ReadAsStringAsync();
        var document = XDocument.Parse(payload);
        var fault = document.Descendants().FirstOrDefault(element => element.Name.LocalName == "faultstring");
        if (fault is not null) throw new InvalidOperationException(fault.Value);
        response.EnsureSuccessStatusCode();
        return document.Root!.Element(soap + "Body")!.Elements().First();
    }

    private static XElement Result(XElement response, string name) => response.Descendants().First(element => element.Name.LocalName == name);
    private static IEnumerable<XElement> ResultItems(XElement response, string resultName, string itemName) =>
        Result(response, resultName).Elements().Where(element => element.Name.LocalName == itemName);
    private static string Text(XElement element, string name) => element.Elements().FirstOrDefault(child => child.Name.LocalName == name)?.Value ?? "";
    private static decimal Decimal(XElement element, string name) => decimal.TryParse(Text(element, name), NumberStyles.Number, CultureInfo.InvariantCulture, out var value) ? value : 0;
    private static int Int(XElement element, string name) => int.TryParse(Text(element, name), NumberStyles.Integer, CultureInfo.InvariantCulture, out var value) ? value : 0;
}
