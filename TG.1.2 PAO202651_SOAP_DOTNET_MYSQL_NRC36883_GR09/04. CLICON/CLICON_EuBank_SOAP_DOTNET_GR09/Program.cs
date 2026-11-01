using CLICON_EuBank_SOAP_DOTNET_GR09;
using CLICON_EuBank_SOAP_DOTNET_GR09.Models;
using CLICON_EuBank_SOAP_DOTNET_GR09.Views;

Console.OutputEncoding = System.Text.Encoding.UTF8;
Console.ResetColor();
try { Console.Clear(); } catch (IOException) { }
var view = new ConsoleView();
view.ShowWelcome();

var authenticatedUser = "";
for (var attempt = 1; attempt <= 3; attempt++)
{
    var (user, password) = view.GetLoginCredentials();
    try
    {
        if (await SoapBankClient.LoginAsync(user ?? "", password ?? ""))
        {
            authenticatedUser = user ?? "";
            break;
        }
        view.ShowLoginError($"Autenticación fallida. Intento {attempt}/3.");
    }
    catch (Exception exception)
    {
        view.ShowError($"No se pudo conectar con el servidor SOAP en http://localhost:9091: {exception.Message}");
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
    try
    {
        switch (view.ShowDashboardMenu())
        {
            case 1:
                view.DisplayCuentas(await SoapBankClient.AccountsAsync());
                Continue();
                break;
            case 2:
            {
                var account = view.GetMovimientoFilter()?.Trim();
                var movements = await SoapBankClient.MovementsAsync(account);
                view.DisplayMovimientos(movements, string.IsNullOrWhiteSpace(account));
                Continue();
                break;
            }
            case 3:
            case 4:
            case 5:
            {
                var types = await SoapBankClient.MovementTypesAsync();
                var request = view.GetCrearMovimientoData(types);
                if (request is null) { Continue(); break; }
                var error = await SoapBankClient.ProcessMovementAsync(request);
                if (error is null) view.ShowMessage("Movimiento registrado exitosamente.");
                else view.ShowError(error);
                Continue();
                break;
            }
            case 6:
                running = false;
                break;
            default:
                view.ShowError("Opción inválida.");
                Continue();
                break;
        }
    }
    catch (Exception exception)
    {
        view.ShowError($"Error al comunicarse con el servidor SOAP: {exception.Message}");
        Continue();
    }
}

static void Continue()
{
    Console.WriteLine("\nPresione Enter para continuar...");
    Console.ReadLine();
}
