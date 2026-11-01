using System.Runtime.Serialization;

namespace WS_EuBank_SOAPDOTNET_GR09.Contracts;

[DataContract(Namespace = SoapNames.ContractNamespace)]
public sealed class CuentaData
{
    [DataMember(Order = 1)] public string Codigo { get; set; } = "";
    [DataMember(Order = 2)] public string Moneda { get; set; } = "";
    [DataMember(Order = 3)] public decimal Saldo { get; set; }
    [DataMember(Order = 4)] public string Estado { get; set; } = "";
    [DataMember(Order = 5)] public string NombreCliente { get; set; } = "";
    [DataMember(Order = 6)] public string? EmailCliente { get; set; }
    [DataMember(Order = 7)] public string? TelefonoCliente { get; set; }
}

[DataContract(Namespace = SoapNames.ContractNamespace)]
public sealed class MovimientoData
{
    [DataMember(Order = 1)] public int Numero { get; set; }
    [DataMember(Order = 2)] public string Fecha { get; set; } = "";
    [DataMember(Order = 3)] public string Tipo { get; set; } = "";
    [DataMember(Order = 4)] public decimal Importe { get; set; }
    [DataMember(Order = 5)] public string? Referencia { get; set; }
    [DataMember(Order = 6)] public string CodigoCuenta { get; set; } = "";
    [DataMember(Order = 7)] public string NombreCliente { get; set; } = "";
    [DataMember(Order = 8)] public decimal SaldoActual { get; set; }
    [DataMember(Order = 9)] public long OrdenCreacion { get; set; }
}

[DataContract(Namespace = SoapNames.ContractNamespace)]
public sealed class ResultadoOperacion
{
    [DataMember(Order = 1)] public int Codigo { get; set; }
    [DataMember(Order = 2)] public string Mensaje { get; set; } = "";
}

public static class SoapNames
{
    public const string ContractNamespace = "http://monster.edu.ec/eurekabank/contracts";
    public const string LoginNamespace = "http://monster.edu.ec/eurekabank/login";
    public const string MovimientosNamespace = "http://monster.edu.ec/eurekabank/movimientos";
}
