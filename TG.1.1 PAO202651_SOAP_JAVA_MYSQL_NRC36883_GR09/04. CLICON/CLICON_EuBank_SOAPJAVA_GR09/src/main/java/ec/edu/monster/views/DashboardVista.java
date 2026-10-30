package ec.edu.monster.views;

import ec.edu.monster.ws.eurekabank.*; 
import ec.edu.monster.controladores.ConsolaController;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList; 
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import org.springframework.stereotype.Component;

@Component
public class DashboardVista {

    private final ConsolaController controller;
    private final Scanner scanner;
    private final DecimalFormat decimalFormat;

    public DashboardVista(ConsolaController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in, "UTF-8");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        this.decimalFormat = new DecimalFormat("#,##0.00", symbols);
    }

    public void mostrarMenuPrincipal() {
        while (true) {
            limpiarPantalla();
            ConsoleTheme.banner("Dashboard - EurekaBank (SOAP)");
            System.out.println("\n1. Ver Cuentas de Clientes");
            System.out.println("2. Ver Movimientos (por cuenta o todos)"); 
            System.out.println("3. Registrar Deposito");
            System.out.println("4. Registrar Retiro");
            System.out.println("5. Registrar Transferencia");
            System.out.println("6. Salir");
            System.out.print("\nSeleccione una opcion: ");

            int opcion;
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                mostrarError("Opcion no valida.");
                esperarTecla();
                continue;
            }

            switch (opcion) {
                case 1:
                    mostrarCuentasClientes();
                    break;
                case 2:
                    mostrarMovimientos(); 
                    break;
                case 3:
                    registrarMovimiento("DEPOSITO");
                    break;
                case 4:
                    registrarMovimiento("RETIRO");
                    break;
                case 5:
                    registrarTransferencia();
                    break;
                case 6:
                    System.out.println("\nGracias por usar EurekaBank. Adios!");
                    return;
                default:
                    mostrarError("Opcion no valida.");
                    esperarTecla();
                    break;
            }
        }
    }

    private void mostrarCuentasClientes() {
        limpiarPantalla();
        ConsoleTheme.title("Cuentas de Clientes");
        try {
            System.out.print("Cargando cuentas...");
            List<DatosCuenta> cuentas = controller.traerCuentas();
            System.out.println(" [OK]");
            if (cuentas.isEmpty()) {
                System.out.println("No se encontraron cuentas.");
            } else {
                String header = String.format("%-10s | %-30.30s | %-10s | %-10s | %12s",
                        "CUENTA", "CLIENTE", "MONEDA", "ESTADO", "SALDO");
                System.out.println(header);
                System.out.println("-".repeat(header.length()));
                for (DatosCuenta c : cuentas) {
                    System.out.printf("%-10s | %-30.30s | %-10s | %-10s | %12s%n",
                            c.getCodigo(),
                            c.getNombreCliente(),
                            c.getMoneda(),
                            c.getEstado(),
                            decimalFormat.format(c.getSaldo()));
                }
            }
        } catch (Exception e) {
            mostrarError("Error al cargar cuentas: " + e.getMessage());
        }
        esperarTecla();
    }

    private void mostrarMovimientos() {
        limpiarPantalla();
        ConsoleTheme.title("Movimientos por Cuenta");
        System.out.print("Ingrese el numero de cuenta (ej. 00100001) o presione Enter para ver todos: ");
        String cuenta = scanner.nextLine().trim();

        if (cuenta.isEmpty()) {
            mostrarTodosLosMovimientos();
        } else {
            mostrarMovimientosDeUnaCuenta(cuenta);
        }
        esperarTecla();
    }

    private void mostrarTodosLosMovimientos() {
        System.out.print("Cargando todas las cuentas...");
        try {
            List<DatosCuenta> cuentas = controller.traerCuentas();
            System.out.println(" [OK]");
            List<MovimientoData> todosMovimientos = new ArrayList<>();
            
            System.out.print("Cargando todos los movimientos...");
            for (DatosCuenta cuenta : cuentas) {
                try {
                    todosMovimientos.addAll(controller.traerMovimientos(cuenta.getCodigo()));
                } catch (Exception ex) {
                }
            }
            System.out.println(" [OK]");

            if (todosMovimientos.isEmpty()) {
                System.out.println("No se encontraron movimientos en ninguna cuenta.");
                return;
            }
            
            todosMovimientos.sort((m1, m2) -> {
                int resFecha = m2.getFecha().toGregorianCalendar().compareTo(m1.getFecha().toGregorianCalendar());
                if (resFecha != 0) {
                    return resFecha;
                }
                return Integer.compare(m2.getNumero(), m1.getNumero());
            });

            String header = String.format("%-10s | %-4s | %-12s | %-25.25s | %12s | %12s",
                    "CUENTA", "NRO", "FECHA", "TIPO", "IMPORTE", "SALDO");
            System.out.println(header);
            System.out.println("-".repeat(header.length()));
            
            for (MovimientoData m : todosMovimientos) {
                String tipo = m.getTipo() != null ? m.getTipo() : "N/A"; 
                System.out.printf("%-10s | %-4d | %-12s | %-25.25s | %12s | %12s%n",
                        m.getCodigoCuenta(), 
                        m.getNumero(),
                        m.getFecha().toString().substring(0, 10),
                        tipo,
                        decimalFormat.format(m.getImporte()),
                        decimalFormat.format(m.getSaldoActual()));
            }

        } catch (Exception e) {
            System.out.println(" [ERROR]");
            mostrarError("Error al cargar todos los movimientos: " + e.getMessage());
        }
    }
    
    private void mostrarMovimientosDeUnaCuenta(String cuenta) {
        try {
            System.out.print("Cargando movimientos de la cuenta " + cuenta + "...");
            List<MovimientoData> movimientos = controller.traerMovimientos(cuenta);
            System.out.println(" [OK]");
            
            if (movimientos.isEmpty()) {
                System.out.println("No se encontraron movimientos para la cuenta " + cuenta);
            } else {
                
                String header = String.format("%-4s | %-12s | %-25.25s | %12s | %12s",
                        "NRO", "FECHA", "TIPO", "IMPORTE", "SALDO");
                System.out.println(header);
                System.out.println("-".repeat(header.length()));
                
                movimientos.sort((m1, m2) -> Integer.compare(m2.getNumero(), m1.getNumero()));
                
                for (MovimientoData m : movimientos) {
                    String tipo = m.getTipo() != null ? m.getTipo() : "N/A"; 
                    System.out.printf("%-4d | %-12s | %-25.25s | %12s | %12s%n",
                            m.getNumero(),
                            m.getFecha().toString().substring(0, 10),
                            tipo,
                            decimalFormat.format(m.getImporte()),
                            decimalFormat.format(m.getSaldoActual()));
                }
            }
        } catch (Exception e) {
            System.out.println(" [ERROR]");
            mostrarError("Error al cargar movimientos: " + e.getMessage());
        }
    }

    private void registrarMovimiento(String tipo) {
        limpiarPantalla();
        ConsoleTheme.title("Registrar " + tipo);
        
        System.out.print("Numero de Cuenta: ");
        String cuenta = scanner.nextLine().trim();
        System.out.print("Importe: ");
        double importe;
        try {
            importe = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
            if (importe <= 0) {
                 mostrarError("El importe debe ser positivo.");
                 esperarTecla();
                 return;
            }
        } catch (Exception e) {
            mostrarError("Importe no valido.");
            esperarTecla();
            return;
        }

        try {
            System.out.print("Procesando " + tipo + "...");
            ResultadoOperacion res = controller.registrarMovimiento(tipo, cuenta, "", importe);
            System.out.println(" [OK]");
            
            if (res.getCodigo() == 1) { 
                System.out.println("\nOperacion exitosa!");
            } else {
                mostrarError(res.getMensaje());
            }
        } catch (Exception e) {
            System.out.println(" [ERROR]");
            mostrarError("Error al registrar movimiento: " + e.getMessage());
        }
        esperarTecla();
    }
    
    private void registrarTransferencia() {
        limpiarPantalla();
        ConsoleTheme.title("Registrar Transferencia");

        System.out.print("Numero de Cuenta Origen: ");
        String cuentaOrigen = scanner.nextLine().trim();
        System.out.print("Numero de Cuenta Destino: ");
        String cuentaDestino = scanner.nextLine().trim();
        System.out.print("Importe: ");
        double importe;
        try {
            importe = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
             if (importe <= 0) {
                 mostrarError("El importe debe ser positivo.");
                 esperarTecla();
                 return;
            }
        } catch (Exception e) {
            mostrarError("Importe no valido.");
            esperarTecla();
            return;
        }
        
        if (cuentaOrigen.equals(cuentaDestino)) {
            mostrarError("Las cuentas de origen y destino no pueden ser la misma.");
            esperarTecla();
            return;
        }

        try {
            System.out.print("Procesando Transferencia...");
            ResultadoOperacion res = controller.registrarMovimiento("TRANSFERENCIA", cuentaOrigen, cuentaDestino, importe);
            System.out.println(" [OK]");
            
            if (res.getCodigo() == 1) {
                System.out.println("\nOperacion exitosa!");
            } else {
                mostrarError(res.getMensaje());
            }
        } catch (Exception e) {
            System.out.println(" [ERROR]");
            mostrarError("Error al registrar movimiento: " + e.getMessage());
        }
        esperarTecla();
    }

    private void mostrarError(String mensaje) {
        System.out.print("\u001B[31m"); 
        System.out.println("\n[ERROR] " + mensaje);
        System.out.print("\u001B[0m"); 
    }

    private void esperarTecla() {
        System.out.println("\nPresione Enter para continuar...");
        try {
            scanner.nextLine();
        } catch (Exception e) {
        }
    }

    private void limpiarPantalla() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("\n".repeat(50));
        }
    }
}
