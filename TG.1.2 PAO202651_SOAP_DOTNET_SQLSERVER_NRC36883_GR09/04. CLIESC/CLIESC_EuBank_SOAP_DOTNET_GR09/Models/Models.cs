using System;

namespace CLIESC_EuBank_SOAP_DOTNET_GR09.Models
{
    public class LoginRequest
    {
        public string usuario { get; set; }
        public string clave { get; set; }
    }

    public class LoginResponse
    {
        public bool success { get; set; }
        public string token { get; set; }
        public string message { get; set; }
    }

    public class Account
    {
        public string codigo { get; set; }
        public string nombreCliente { get; set; }
        public string emailCliente { get; set; }
        public string telefonoCliente { get; set; }
        public string moneda { get; set; }
        public decimal saldo { get; set; }
        public string estado { get; set; }
    }

    public class Movement
    {
        public int numero { get; set; }
        public long ordenCreacion { get; set; }
        public DateTime fecha { get; set; }
        public string tipo { get; set; }
        public decimal importe { get; set; }
        public string referencia { get; set; }
        public string codigoCuenta { get; set; }
        public decimal saldoActual { get; set; }
        public string nombreCliente { get; set; }
    }

    public class MovementRequest
    {
        public string tipo { get; set; }
        public string cuentaOrigen { get; set; }
        public string cuentaDestino { get; set; }
        public decimal importe { get; set; }
    }

    public class MovementType
    {
        public string codigo { get; set; }
        public string descripcion { get; set; }
    }
}
