namespace CLICON_EuBank_REST_DOTNET_GR09.Models
{
    public class LoginRequest
    {
        public string usuario { get; set; } = string.Empty;
        public string clave { get; set; } = string.Empty;
    }

    public class LoginResponse
    {
        public bool success { get; set; }
        public string? token { get; set; }
        public string? message { get; set; }
    }

    public class Account
    {
        public string codigo { get; set; } = string.Empty;
        public string nombreCliente { get; set; } = string.Empty;
        public string emailCliente { get; set; } = string.Empty;
        public string telefonoCliente { get; set; } = string.Empty;
        public string moneda { get; set; } = string.Empty;
        public decimal saldo { get; set; }
        public string estado { get; set; } = string.Empty;
    }

    public class Movement
    {
        public int numero { get; set; }
        public long ordenCreacion { get; set; }
        public DateTime fecha { get; set; }
        public string tipo { get; set; } = string.Empty;
        public decimal importe { get; set; }
        public string? referencia { get; set; }
        public string codigoCuenta { get; set; } = string.Empty;
        public decimal saldoActual { get; set; }
        public string nombreCliente { get; set; } = string.Empty;
    }

    public class MovementRequest
    {
        public string tipo { get; set; } = string.Empty;
        public string cuentaOrigen { get; set; } = string.Empty;
        public string? cuentaDestino { get; set; }
        public decimal importe { get; set; }
    }

    public class MovementType
    {
        public string codigo { get; set; } = string.Empty;
        public string descripcion { get; set; } = string.Empty;
    }
}