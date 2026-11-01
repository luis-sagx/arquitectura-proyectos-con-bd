using System.Net.Http.Json;
using System.Text.Json;

namespace CLIMOV_EurekaBank_REST_DOTNET_GR09;

internal static class BankApi
{
    private static readonly HttpClient Http = new() { Timeout = TimeSpan.FromSeconds(20) };
    private static readonly JsonSerializerOptions Json = new() { PropertyNameCaseInsensitive = true };
    private static string BaseUrl
    {
        get
        {
#if ANDROID
            return "http://10.0.2.2:9090/api";
#else
            return "http://localhost:9090/api";
#endif
        }
    }

    public static async Task<bool> Login(string user, string password)
    {
        using var response = await Http.PostAsJsonAsync($"{BaseUrl}/login", new { usuario = user, clave = password });
        if (!response.IsSuccessStatusCode) return false;
        var result = await response.Content.ReadFromJsonAsync<LoginResult>(Json);
        return result?.Success == true;
    }

    public static async Task<List<AccountDto>> Accounts() =>
        await Http.GetFromJsonAsync<List<AccountDto>>($"{BaseUrl}/cuentas", Json) ?? [];

    public static async Task<List<MovementDto>> Movements(string? account = null)
    {
        var url = string.IsNullOrWhiteSpace(account) ? $"{BaseUrl}/movimientos" : $"{BaseUrl}/movimientos?cuenta={Uri.EscapeDataString(account)}";
        return await Http.GetFromJsonAsync<List<MovementDto>>(url, Json) ?? [];
    }

    public static async Task<List<string>> MovementTypes() =>
        await Http.GetFromJsonAsync<List<string>>($"{BaseUrl}/tiposmovimiento", Json) ?? [];

    public static async Task<string?> CreateMovement(string origin, string? destination, string type, decimal amount)
    {
        using var response = await Http.PostAsJsonAsync($"{BaseUrl}/movimientos", new
        {
            cuentaOrigen = origin,
            cuentaDestino = destination,
            tipo = type,
            importe = amount
        });
        if (response.IsSuccessStatusCode) return null;
        try
        {
            var error = await response.Content.ReadFromJsonAsync<ApiError>(Json);
            return error?.Message ?? "No se pudo registrar la operación.";
        }
        catch { return "No se pudo registrar la operación."; }
    }
}

internal sealed class LoginResult { public bool Success { get; set; } }
internal sealed class ApiError { public string? Message { get; set; } }
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
