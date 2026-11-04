using System.Globalization;
using System.Text;
using System.Xml.Linq;

namespace CLIMOV_EurekaBank_SOAP_DOTNET_GR09;

internal static class SoapTransport
{
    internal const string LoginNamespace = "http://monster.edu.ec/eurekabank/login";
    internal const string MovementsNamespace = "http://monster.edu.ec/eurekabank/movimientos";
    private const string SoapNamespace = "http://schemas.xmlsoap.org/soap/envelope/";
    private static readonly HttpClient Http = new() { Timeout = TimeSpan.FromSeconds(20) };

    internal static XElement Argument(string ns, string name, string value) => new(XName.Get(name, ns), value);
    internal static async Task<XElement> CallAsync(string service, string ns, string contract, string operation, params XElement[] arguments)
    {
        XNamespace soap = SoapNamespace;
        var envelope = new XElement(soap + "Envelope", new XAttribute(XNamespace.Xmlns + "soap", soap),
            new XElement(soap + "Body", new XElement(XName.Get(operation, ns), arguments)));
        using var request = new HttpRequestMessage(HttpMethod.Post, $"http://10.0.2.2:9091/Services/{service}")
        {
            Content = new StringContent(envelope.ToString(SaveOptions.DisableFormatting), Encoding.UTF8, "text/xml")
        };
#if !ANDROID
        request.RequestUri = new Uri($"http://localhost:9091/Services/{service}");
#endif
        request.Headers.Add("SOAPAction", $"\"{ns}/{contract}/{operation}\"");
        using var response = await Http.SendAsync(request);
        var text = await response.Content.ReadAsStringAsync();
        var document = XDocument.Parse(text);
        var fault = document.Descendants().FirstOrDefault(element => element.Name.LocalName == "faultstring");
        if (fault is not null) throw new InvalidOperationException(fault.Value);
        response.EnsureSuccessStatusCode();
        return document.Root!.Element(soap + "Body")!.Elements().First();
    }
    internal static XElement Result(XElement response, string name) => response.Descendants().First(element => element.Name.LocalName == name);
    internal static IEnumerable<XElement> Items(XElement response, string resultName, string itemName) => Result(response, resultName).Elements().Where(element => element.Name.LocalName == itemName);
    internal static string Text(XElement item, string name) => item.Elements().FirstOrDefault(child => child.Name.LocalName == name)?.Value ?? "";
    internal static decimal Decimal(XElement item, string name) => decimal.TryParse(Text(item, name), NumberStyles.Number, CultureInfo.InvariantCulture, out var value) ? value : 0;
}
