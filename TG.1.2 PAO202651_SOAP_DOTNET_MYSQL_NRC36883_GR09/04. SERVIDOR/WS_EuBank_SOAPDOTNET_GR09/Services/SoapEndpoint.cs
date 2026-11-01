using System.Globalization;
using System.Text;
using System.Xml.Linq;
using WS_EuBank_SOAPDOTNET_GR09.Contracts;

namespace WS_EuBank_SOAPDOTNET_GR09.Services;

public static class SoapEndpoint
{
    private static readonly XNamespace EnvelopeNamespace = "http://schemas.xmlsoap.org/soap/envelope/";
    private static readonly XNamespace XsiNamespace = "http://www.w3.org/2001/XMLSchema-instance";

    public static void MapSoapServices(this WebApplication app)
    {
        app.MapMethods("/Services/LoginService.svc", ["GET", "POST", "OPTIONS"], context => DispatchAsync(context, true));
        app.MapMethods("/Services/MovimientosService.svc", ["GET", "POST", "OPTIONS"], context => DispatchAsync(context, false));
    }

    private static async Task DispatchAsync(HttpContext context, bool loginService)
    {
        if (HttpMethods.IsOptions(context.Request.Method))
        {
            context.Response.StatusCode = StatusCodes.Status204NoContent;
            return;
        }
        if (HttpMethods.IsGet(context.Request.Method))
        {
            context.Response.ContentType = "text/xml; charset=utf-8";
            await context.Response.WriteAsync(CreateWsdl(loginService), Encoding.UTF8);
            return;
        }

        try
        {
            var request = await XDocument.LoadAsync(context.Request.Body, LoadOptions.None, context.RequestAborted);
            var operation = request.Descendants().FirstOrDefault(element => element.Parent?.Name == EnvelopeNamespace + "Body")
                ?? throw new InvalidOperationException("El mensaje SOAP no contiene una operación.");
            var args = operation.Elements().ToDictionary(element => element.Name.LocalName, element => element.Value, StringComparer.OrdinalIgnoreCase);
            var operationName = operation.Name.LocalName;
            var output = loginService
                ? await InvokeLoginAsync(context, operationName, args)
                : await InvokeMovimientosAsync(context, operationName, args);
            await WriteSoapAsync(context, operation.Name.NamespaceName, operationName + "Response", output, StatusCodes.Status200OK);
        }
        catch (Exception exception)
        {
            var fault = new XElement("Fault",
                new XElement("faultcode", "soap:Server"),
                new XElement("faultstring", exception.Message));
            await WriteSoapAsync(context, "", "", fault, StatusCodes.Status500InternalServerError);
        }
    }

    private static async Task<XElement> InvokeLoginAsync(HttpContext context, string operation, IReadOnlyDictionary<string, string> args)
    {
        if (operation != "Login") throw new InvalidOperationException($"Operación SOAP no reconocida: {operation}.");
        using var scope = context.RequestServices.CreateScope();
        var service = scope.ServiceProvider.GetRequiredService<LoginService>();
        var result = await service.LoginAsync(Value(args, "usuario"), Value(args, "clave"), context.RequestAborted);
        return new XElement("LoginResult", result.ToString().ToLowerInvariant());
    }

    private static async Task<XElement> InvokeMovimientosAsync(HttpContext context, string operation, IReadOnlyDictionary<string, string> args)
    {
        using var scope = context.RequestServices.CreateScope();
        var service = scope.ServiceProvider.GetRequiredService<MovimientosService>();
        return operation switch
        {
            "GetDatosCuentas" => ArrayResult("GetDatosCuentasResult", "CuentaData", await service.GetDatosCuentasAsync(context.RequestAborted), AccountElement),
            "GetDatosCuenta" => SingleResult("GetDatosCuentaResult", await service.GetDatosCuentaAsync(Value(args, "cuenta"), context.RequestAborted), AccountElement),
            "GetMovimientos" => ArrayResult("GetMovimientosResult", "MovimientoData", await service.GetMovimientosAsync(NullableValue(args, "cuenta"), context.RequestAborted), MovementElement),
            "GetTiposMovimiento" => ArrayResult("GetTiposMovimientoResult", "string", await service.GetTiposMovimientoAsync(context.RequestAborted), value => new XElement("string", value)),
            "ProcesarMovimiento" => SingleResult("ProcesarMovimientoResult", await service.ProcesarMovimientoAsync(
                Value(args, "tipo"), Value(args, "cuentaOrigen"), NullableValue(args, "cuentaDestino"),
                decimal.TryParse(Value(args, "importe"), NumberStyles.Number, CultureInfo.InvariantCulture, out var amount) ? amount : 0,
                context.RequestAborted), ResultElement),
            _ => throw new InvalidOperationException($"Operación SOAP no reconocida: {operation}.")
        };
    }

    private static string Value(IReadOnlyDictionary<string, string> args, string name) => args.GetValueOrDefault(name) ?? "";
    private static string? NullableValue(IReadOnlyDictionary<string, string> args, string name) =>
        string.IsNullOrWhiteSpace(args.GetValueOrDefault(name)) ? null : args.GetValueOrDefault(name);

    private static XElement ArrayResult<T>(string resultName, string itemName, IEnumerable<T> values, Func<T, XElement> serialize) =>
        new(resultName, values.Select(value => serialize(value)));

    private static XElement SingleResult<T>(string resultName, T? value, Func<T, XElement> serialize) where T : class =>
        value is null ? new XElement(resultName, new XAttribute(XsiNamespace + "nil", "true")) : new XElement(resultName, serialize(value));

    private static XElement AccountElement(CuentaData account) => new("CuentaData",
        new XElement("Codigo", account.Codigo), new XElement("Moneda", account.Moneda),
        new XElement("Saldo", account.Saldo.ToString(CultureInfo.InvariantCulture)), new XElement("Estado", account.Estado),
        new XElement("NombreCliente", account.NombreCliente), new XElement("EmailCliente", account.EmailCliente ?? ""),
        new XElement("TelefonoCliente", account.TelefonoCliente ?? ""));

    private static XElement MovementElement(MovimientoData movement) => new("MovimientoData",
        new XElement("Numero", movement.Numero), new XElement("Fecha", movement.Fecha), new XElement("Tipo", movement.Tipo),
        new XElement("Importe", movement.Importe.ToString(CultureInfo.InvariantCulture)),
        new XElement("Referencia", movement.Referencia ?? ""), new XElement("CodigoCuenta", movement.CodigoCuenta),
        new XElement("NombreCliente", movement.NombreCliente), new XElement("SaldoActual", movement.SaldoActual.ToString(CultureInfo.InvariantCulture)),
        new XElement("OrdenCreacion", movement.OrdenCreacion));

    private static XElement ResultElement(ResultadoOperacion result) => new("ResultadoOperacion",
        new XElement("Codigo", result.Codigo), new XElement("Mensaje", result.Mensaje));

    private static async Task WriteSoapAsync(HttpContext context, string operationNamespace, string responseName, XElement result, int statusCode)
    {
        XNamespace resultNamespace = operationNamespace;
        var responseBody = responseName.Length == 0
            ? result
            : new XElement(resultNamespace + responseName,
                new XElement(resultNamespace + result.Name.LocalName, result.Attributes(), result.Nodes()));
        var document = new XDocument(new XDeclaration("1.0", "utf-8", null),
            new XElement(EnvelopeNamespace + "Envelope",
                new XAttribute(XNamespace.Xmlns + "soap", EnvelopeNamespace),
                new XElement(EnvelopeNamespace + "Body", responseBody)));
        context.Response.StatusCode = statusCode;
        context.Response.ContentType = "text/xml; charset=utf-8";
        await context.Response.WriteAsync(document.ToString(SaveOptions.DisableFormatting), Encoding.UTF8, context.RequestAborted);
    }

    private static string CreateWsdl(bool login)
    {
        var serviceName = login ? "LoginService" : "MovimientosService";
        var contractName = login ? "ILoginService" : "IMovimientosService";
        var ns = login ? SoapNames.LoginNamespace : SoapNames.MovimientosNamespace;
        var endpoint = $"http://localhost:9091/Services/{serviceName}.svc";
        var operationDefinitions = login
            ? "<xs:element name=\"Login\"><xs:complexType><xs:sequence><xs:element name=\"usuario\" type=\"xs:string\"/><xs:element name=\"clave\" type=\"xs:string\"/></xs:sequence></xs:complexType></xs:element><xs:element name=\"LoginResponse\"><xs:complexType><xs:sequence><xs:element name=\"LoginResult\" type=\"xs:boolean\"/></xs:sequence></xs:complexType></xs:element>"
            : "<xs:element name=\"GetDatosCuentas\"><xs:complexType/></xs:element><xs:element name=\"GetDatosCuentasResponse\"><xs:complexType><xs:sequence><xs:element name=\"GetDatosCuentasResult\" type=\"xs:anyType\"/></xs:sequence></xs:complexType></xs:element><xs:element name=\"GetDatosCuenta\"><xs:complexType><xs:sequence><xs:element name=\"cuenta\" type=\"xs:string\"/></xs:sequence></xs:complexType></xs:element><xs:element name=\"GetDatosCuentaResponse\"><xs:complexType><xs:sequence><xs:element name=\"GetDatosCuentaResult\" minOccurs=\"0\" type=\"xs:anyType\"/></xs:sequence></xs:complexType></xs:element><xs:element name=\"GetMovimientos\"><xs:complexType><xs:sequence><xs:element name=\"cuenta\" type=\"xs:string\" minOccurs=\"0\" nillable=\"true\"/></xs:sequence></xs:complexType></xs:element><xs:element name=\"GetMovimientosResponse\"><xs:complexType><xs:sequence><xs:element name=\"GetMovimientosResult\" type=\"xs:anyType\"/></xs:sequence></xs:complexType></xs:element><xs:element name=\"GetTiposMovimiento\"><xs:complexType/></xs:element><xs:element name=\"GetTiposMovimientoResponse\"><xs:complexType><xs:sequence><xs:element name=\"GetTiposMovimientoResult\" type=\"xs:anyType\"/></xs:sequence></xs:complexType></xs:element><xs:element name=\"ProcesarMovimiento\"><xs:complexType><xs:sequence><xs:element name=\"tipo\" type=\"xs:string\"/><xs:element name=\"cuentaOrigen\" type=\"xs:string\"/><xs:element name=\"cuentaDestino\" type=\"xs:string\" minOccurs=\"0\" nillable=\"true\"/><xs:element name=\"importe\" type=\"xs:decimal\"/></xs:sequence></xs:complexType></xs:element><xs:element name=\"ProcesarMovimientoResponse\"><xs:complexType><xs:sequence><xs:element name=\"ProcesarMovimientoResult\" type=\"xs:anyType\"/></xs:sequence></xs:complexType></xs:element>";
        var operations = login ? new[] { "Login" } : new[] { "GetDatosCuentas", "GetDatosCuenta", "GetMovimientos", "GetTiposMovimiento", "ProcesarMovimiento" };
        var portType = string.Join("", operations.Select(op => $"<wsdl:operation name=\"{op}\"><wsdl:input message=\"tns:{op}Input\"/><wsdl:output message=\"tns:{op}Output\"/></wsdl:operation>"));
        var messages = string.Join("", operations.Select(op => $"<wsdl:message name=\"{op}Input\"><wsdl:part name=\"parameters\" element=\"tns:{op}\"/></wsdl:message><wsdl:message name=\"{op}Output\"><wsdl:part name=\"parameters\" element=\"tns:{op}Response\"/></wsdl:message>"));
        var bindings = string.Join("", operations.Select(op => $"<wsdl:operation name=\"{op}\"><soap:operation soapAction=\"{ns}/{contractName}/{op}\" style=\"document\"/><wsdl:input><soap:body use=\"literal\"/></wsdl:input><wsdl:output><soap:body use=\"literal\"/></wsdl:output></wsdl:operation>"));
        return $"<?xml version=\"1.0\" encoding=\"utf-8\"?><wsdl:definitions xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:tns=\"{ns}\" targetNamespace=\"{ns}\"><wsdl:types><xs:schema targetNamespace=\"{ns}\" elementFormDefault=\"qualified\">{operationDefinitions}</xs:schema></wsdl:types>{messages}<wsdl:portType name=\"{contractName}\">{portType}</wsdl:portType><wsdl:binding name=\"{contractName}Soap\" type=\"tns:{contractName}\"><soap:binding transport=\"http://schemas.xmlsoap.org/soap/http\" style=\"document\"/>{bindings}</wsdl:binding><wsdl:service name=\"{serviceName}\"><wsdl:port name=\"{contractName}Soap\" binding=\"tns:{contractName}Soap\"><soap:address location=\"{endpoint}\"/></wsdl:port></wsdl:service></wsdl:definitions>";
    }
}
