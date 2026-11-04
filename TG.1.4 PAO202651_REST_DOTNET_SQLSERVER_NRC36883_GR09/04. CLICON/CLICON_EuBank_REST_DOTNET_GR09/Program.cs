using System.Net.Http.Json;
using System.Text.Json;
using CLICON_EuBank_REST_DOTNET_GR09.Models;
using CLICON_EuBank_REST_DOTNET_GR09.Views;

Console.OutputEncoding = System.Text.Encoding.UTF8;
Console.ResetColor();
try { Console.Clear(); }
catch (IOException) { }
var view = new ConsoleView();
using var http = new HttpClient { BaseAddress = new Uri("http://localhost:9090/api/") };
var json = new JsonSerializerOptions(JsonSerializerDefaults.Web) { PropertyNameCaseInsensitive = true };

view.ShowWelcome();
var authenticatedUser = "";
for (var attempt = 1; attempt <= 3; attempt++)
{
    var (user, password) = view.GetLoginCredentials();
    try
    {
        using var response = await http.PostAsJsonAsync("login", new { usuario = user ?? "", clave = password ?? "" });
        var result = await response.Content.ReadFromJsonAsync<LoginResponse>(json);
        if (response.IsSuccessStatusCode && result?.success == true)
        {
            authenticatedUser = user ?? "";
            break;
        }
        view.ShowLoginError($"Autenticación fallida. Intento {attempt}/3.");
    }
    catch (HttpRequestException ex)
    {
        view.ShowError($"No se pudo conectar con http://localhost:9090: {ex.Message}");
        return;
    }
    if (attempt < 3)
    {
        Console.Write("¿Deseas intentar de nuevo? (s/n): ");
        if (!string.Equals(Console.ReadLine()?.Trim(), "s", StringComparison.OrdinalIgnoreCase)) return;
    }
}
if (string.IsNullOrEmpty(authenticatedUser))
{
    view.ShowLoginError("Máximo de intentos alcanzado.");
    return;
}
view.ShowLoginSuccess(authenticatedUser);
Continue();

var running = true;
while (running)
{
    switch (view.ShowDashboardMenu())
    {
        case 6:
            running = false;
            break;
        case 1:
        {
            var accounts = await http.GetFromJsonAsync<List<Account>>("cuentas", json) ?? [];
            view.DisplayCuentas(accounts);
            Continue();
            break;
        }
        case 2:
        {
            var accountCode = view.GetMovimientoFilter()?.Trim();
            var path = "movimientos" + (string.IsNullOrWhiteSpace(accountCode) ? "" : $"?cuenta={Uri.EscapeDataString(accountCode)}");
            var movements = await http.GetFromJsonAsync<List<Movement>>(path, json) ?? [];
            view.DisplayMovimientos(movements, string.IsNullOrWhiteSpace(accountCode));
            Continue();
            break;
        }
        case 3:
            await RegisterMovement("DEPOSITO");
            break;
        case 4:
            await RegisterMovement("RETIRO");
            break;
        case 5:
            await RegisterMovement("TRANSFERENCIA");
            break;
        default:
            view.ShowError("Opción inválida.");
            Continue();
            break;
    }
}

async Task RegisterMovement(string type)
{
    var request = view.GetCrearMovimientoData(type);
    if (request is null) { Continue(); return; }
    try
    {
        using var response = await http.PostAsJsonAsync("movimientos", request, json);
        var result = await response.Content.ReadFromJsonAsync<JsonElement>(json);
        var success = response.IsSuccessStatusCode && result.TryGetProperty("success", out var flag) && flag.GetBoolean();
        if (success) view.ShowMessage("\nOperación exitosa: movimiento registrado.");
        else view.ShowError(result.TryGetProperty("message", out var message) ? message.GetString() ?? "No se pudo registrar el movimiento." : "No se pudo registrar el movimiento.");
    }
    catch (HttpRequestException ex) { view.ShowError($"Error al conectar con el servidor: {ex.Message}"); }
    Continue();
}

static void Continue()
{
    Console.WriteLine("\nPresione Enter para continuar...");
    Console.ReadLine();
}
