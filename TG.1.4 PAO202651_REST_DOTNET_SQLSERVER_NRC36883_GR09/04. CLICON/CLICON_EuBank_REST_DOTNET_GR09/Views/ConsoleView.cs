using CLICON_EuBank_REST_DOTNET_GR09.Models;
using System.Globalization;

namespace CLICON_EuBank_REST_DOTNET_GR09.Views;

public class ConsoleView
{
    public void ShowWelcome()
    {
        Console.WriteLine("---------------------------------------------");
        Console.WriteLine("Cliente REST - EurekaBank (Consola)");
        Console.WriteLine("---------------------------------------------");
        WriteBanner("Sistema de Login - EurekaBank");
        Console.ResetColor();
    }

    public (string? usuario, string? clave) GetLoginCredentials()
    {
        Console.ForegroundColor = ConsoleColor.White;
        Console.WriteLine("------------------------------");
        Console.Write("Usuario: ");
        Console.ResetColor();
        string? usuario = Console.ReadLine();
        Console.ForegroundColor = ConsoleColor.White;
        Console.Write("Clave: ");
        Console.ResetColor();
        string? clave = Console.ReadLine();
        Console.ForegroundColor = ConsoleColor.White;
        Console.WriteLine("------------------------------");
        Console.ResetColor();
        return (usuario, clave);
    }

    public void ShowLoginSuccess(string user)
    {
        Console.ForegroundColor = ConsoleColor.Green;
        Console.WriteLine($"Bienvenido, {user}!");
        Console.WriteLine("========================");
        Console.ResetColor();
    }

    public void ShowLoginError(string message)
    {
        Console.ForegroundColor = ConsoleColor.Red;
        Console.WriteLine($"[ERROR] {message}");
        Console.ResetColor();
    }

    public void ShowError(string message)
    {
        Console.ForegroundColor = ConsoleColor.Red;
        Console.WriteLine($"[ERROR] {message}");
        Console.ResetColor();
    }

    public int ShowDashboardMenu()
    {
        ClearScreen();
        WriteBanner("Dashboard - EurekaBank (REST)");
        Console.ResetColor();
        Console.WriteLine("1. Ver Cuentas de Clientes");
        Console.WriteLine("2. Ver Movimientos (por cuenta o todos)");
        Console.WriteLine("3. Registrar Deposito");
        Console.WriteLine("4. Registrar Retiro");
        Console.WriteLine("5. Registrar Transferencia");
        Console.WriteLine("6. Salir");
        Console.ForegroundColor = ConsoleColor.White;
        Console.Write("Seleccione una opcion: ");
        Console.ResetColor();
        string? input = Console.ReadLine();
        return int.TryParse(input, out int opcion) ? opcion : -1;
    }

    public void DisplayCuentas(List<Account> cuentas)
    {
        ClearScreen();
        if (cuentas.Any())
        {
            Console.ForegroundColor = ConsoleColor.White;
            Console.WriteLine("Cuentas de Clientes");
            Console.ResetColor();
            Console.ForegroundColor = ConsoleColor.White;
            string separator = new string('-', 78);
            Console.WriteLine(separator);
            Console.WriteLine("CUENTA     | CLIENTE                        | MONEDA     | ESTADO     |        SALDO");
            Console.WriteLine(separator);
            Console.ResetColor();
            foreach (var c in cuentas)
            {
                Console.WriteLine(
                    $"{c.codigo,-10} | " +
                    $"{Truncate(c.nombreCliente, 30),-30} | " +
                    $"{c.moneda,-10} | " +
                    $"{c.estado,-10} | " +
                    $"{c.saldo.ToString("#,##0.00", CultureInfo.InvariantCulture),12}");
            }
            Console.ForegroundColor = ConsoleColor.White;
            Console.WriteLine(separator);
            Console.ResetColor();
        }
        else
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine("No hay cuentas disponibles");
            Console.ResetColor();
        }
        Console.WriteLine();
    }
    private static string Truncate(string? value, int maxLength)
    {
        if (string.IsNullOrEmpty(value))
        {
            return "";
        }

        return value.Length <= maxLength ? value : value.Substring(0, maxLength);
    }

    public string? GetMovimientoFilter()
    {
        ClearScreen();
        WriteBanner("Movimientos por Cuenta");
        Console.ForegroundColor = ConsoleColor.White;
        Console.Write("Ingrese el numero de cuenta (ej. 00100001) o presione Enter para ver todos: ");
        Console.ResetColor();
        return Console.ReadLine();
    }

    public void DisplayMovimientos(List<Movement> movimientos, bool incluirCuenta = true)
    {
        Console.WriteLine();
        if (movimientos.Any())
        {
            Console.ForegroundColor = ConsoleColor.White;
            Console.WriteLine("Movimientos por Cuenta");
            Console.ResetColor();
            Console.ForegroundColor = ConsoleColor.White;
            string separator = new string('-', incluirCuenta ? 83 : 69);
            Console.WriteLine(separator);
            Console.WriteLine(incluirCuenta
                ? "CUENTA     | NRO  | FECHA        | TIPO                      |      IMPORTE |        SALDO"
                : "NRO  | FECHA        | TIPO                      |      IMPORTE |        SALDO");
            Console.WriteLine(separator);
            Console.ResetColor();
            var sortedMovimientos = movimientos.OrderByDescending(m => m.fecha).ThenByDescending(m => m.ordenCreacion).ToList();
            foreach (var m in sortedMovimientos)
            {
                var accountColumn = incluirCuenta ? $"{m.codigoCuenta,-10} | " : "";
                Console.WriteLine(accountColumn +
                    $"{m.numero,-4} | " +
                    $"{m.fecha:yyyy-MM-dd} | " +
                    $"{Truncate(m.tipo, 25),-25} | " +
                    $"{m.importe.ToString("#,##0.00", CultureInfo.InvariantCulture),12} | " +
                    $"{m.saldoActual.ToString("#,##0.00", CultureInfo.InvariantCulture),12}");
            }
            Console.ForegroundColor = ConsoleColor.White;
            Console.WriteLine(separator);
            Console.ResetColor();
        }
        else
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine("No hay movimientos");
            Console.ResetColor();
        }
        Console.WriteLine();
    }

    public MovementRequest? GetCrearMovimientoData(List<string> tipos)
    {
        ClearScreen();
        Console.ForegroundColor = ConsoleColor.White;
        Console.WriteLine("Crear Nuevo Movimiento");
        Console.WriteLine("======================");
        Console.ResetColor();
        Console.WriteLine("Tipos disponibles:");
        for (int i = 0; i < tipos.Count; i++)
        {
            Console.WriteLine($"{i + 1}. {tipos[i]}");
        }

        Console.ForegroundColor = ConsoleColor.White;
        Console.Write("Seleccione tipo (numero): ");
        Console.ResetColor();
        if (!int.TryParse(Console.ReadLine(), out int tipoIndex) || tipoIndex < 1 || tipoIndex > tipos.Count)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine("Seleccion invalida");
            Console.ResetColor();
            return null;
        }

        string tipo = tipos[tipoIndex - 1];

        Console.ForegroundColor = ConsoleColor.White;
        Console.Write("Cuenta Origen: ");
        Console.ResetColor();
        string? origen = Console.ReadLine();
        if (string.IsNullOrEmpty(origen))
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine("Cuenta origen requerida");
            Console.ResetColor();
            return null;
        }

        string? destino = null;
        if (tipo == "TRANSFERENCIA")
        {
            Console.ForegroundColor = ConsoleColor.White;
            Console.Write("Cuenta Destino: ");
            Console.ResetColor();
            destino = Console.ReadLine();
            if (string.IsNullOrEmpty(destino))
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine("Cuenta destino requerida para transferencias");
                Console.ResetColor();
                return null;
            }
        }

        Console.ForegroundColor = ConsoleColor.White;
        Console.Write("Monto: ");
        Console.ResetColor();
        if (!decimal.TryParse(Console.ReadLine(), out decimal importe) || importe <= 0)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine("Monto invalido");
            Console.ResetColor();
            return null;
        }

        return new MovementRequest { tipo = tipo, cuentaOrigen = origen, cuentaDestino = destino, importe = importe };
    }

    public MovementRequest? GetCrearMovimientoData(string tipo)
    {
        ClearScreen();
        Console.ForegroundColor = ConsoleColor.White;
        var displayType = tipo switch { "DEPOSITO" => "Deposito", "RETIRO" => "Retiro", "TRANSFERENCIA" => "Transferencia", _ => tipo };
        Console.WriteLine($"Registrar {displayType}");
        Console.WriteLine(new string('=', 20 + tipo.Length));
        Console.ResetColor();
        Console.Write(tipo == "TRANSFERENCIA" ? "Numero de Cuenta Origen: " : "Numero de Cuenta: ");
        string? origen = Console.ReadLine()?.Trim();
        if (string.IsNullOrWhiteSpace(origen))
        {
            ShowError("La cuenta de origen es obligatoria.");
            return null;
        }

        string? destino = null;
        if (tipo == "TRANSFERENCIA")
        {
            Console.Write("Número de cuenta de destino: ");
            destino = Console.ReadLine()?.Trim();
            if (string.IsNullOrWhiteSpace(destino) || destino == origen)
            {
                ShowError("Ingresa una cuenta destino válida y distinta a la de origen.");
                return null;
            }
        }

        Console.Write("Importe: ");
        if (!decimal.TryParse(Console.ReadLine()?.Trim().Replace(',', '.'), System.Globalization.NumberStyles.Number, System.Globalization.CultureInfo.InvariantCulture, out var importe) || importe <= 0)
        {
            ShowError("El importe debe ser un número mayor a cero.");
            return null;
        }

        return new MovementRequest { tipo = tipo, cuentaOrigen = origen, cuentaDestino = destino, importe = importe };
    }

    public void ShowMessage(string message)
    {
        Console.WriteLine(message);
    }

    private static void WriteBanner(string title)
    {
        var border = new string('-', title.Length + 2);
        Console.ForegroundColor = ConsoleColor.White;
        Console.WriteLine($"\n{border}\n {title} \n{border}");
        Console.ResetColor();
    }

    private static void ClearScreen()
    {
        try { Console.Clear(); }
        catch (IOException) { Console.WriteLine(); }
    }
}
