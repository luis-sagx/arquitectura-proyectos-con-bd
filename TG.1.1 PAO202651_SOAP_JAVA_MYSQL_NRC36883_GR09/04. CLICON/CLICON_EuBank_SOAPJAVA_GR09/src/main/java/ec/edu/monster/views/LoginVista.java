package ec.edu.monster.views;

import ec.edu.monster.models.Usuario;
import ec.edu.monster.controladores.ConsolaController;
import java.util.Scanner;
import org.springframework.stereotype.Component; 

@Component 
public class LoginVista {

    private final ConsolaController controller;
    private final Scanner scanner;

    public LoginVista(ConsolaController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in, "UTF-8");
    }

    public Usuario mostrarLogin() {
        limpiarPantalla();
        ConsoleTheme.banner("Sistema de Login - EurekaBank");

        int intentos = 0;
        final int MAX_INTENTOS = 3;

        while (intentos < MAX_INTENTOS) {
            System.out.print("\nUsuario: ");
            String username = scanner.nextLine().trim();

            if (username.isEmpty()) {
                mostrarError("El usuario no puede estar vacio.");
                esperarTecla();
                limpiarPantalla();
                ConsoleTheme.banner("Sistema de Login - EurekaBank");
                continue;
            }

            System.out.print("Contrasena: ");
            String password = scanner.nextLine().trim();

            if (password.isEmpty()) {
                mostrarError("La contrasena no puede estar vacia.");
                esperarTecla();
                limpiarPantalla();
                ConsoleTheme.banner("Sistema de Login - EurekaBank");
                continue;
            }

            System.out.print("\nAutenticando (SOAP)...");
            Usuario usuario = new Usuario(username, password);
            try {
                if (controller.autenticar(usuario.getUsername(), usuario.getPassword())) {
                    System.out.println(" [OK]");
                    System.out.println("\nAutenticacion exitosa. Bienvenido, " + username + "!");
                    usuario.setAutenticado(true); 
                    esperarTecla();
                    return usuario;
                } else {
                    System.out.println(" [ERROR]");
                    intentos++;
                    String mensaje = "Autenticacion fallida. Intente de nuevo. (Intento " + intentos + "/" + MAX_INTENTOS + ")";
                    mostrarError(mensaje);
                    if (intentos >= MAX_INTENTOS) {
                        mostrarError("Maximo de intentos alcanzado.");
                        esperarTecla();
                        return null;
                    }
                    System.out.print("Desea intentar de nuevo? (s/n): ");
                    String opcion = scanner.nextLine().trim().toLowerCase();
                    if (!opcion.equals("s")) {
                        return null;
                    }
                    limpiarPantalla();
                    ConsoleTheme.banner("Sistema de Login - EurekaBank");
                }
            } catch (Exception e) {
                System.out.println(" [ERROR]");
                intentos++;
                String mensaje = "Error inesperado: " + e.getMessage() + " (Intento " + intentos + "/" + MAX_INTENTOS + ")";
                mostrarError(mensaje);
                if (intentos >= MAX_INTENTOS) {
                    mostrarError("Maximo de intentos alcanzado.");
                    esperarTecla();
                    return null;
                }
                System.out.print("Desea intentar de nuevo? (s/n): ");
                String opcion = scanner.nextLine().trim().toLowerCase();
                if (!opcion.equals("s")) {
                    return null;
                }
                limpiarPantalla();
                ConsoleTheme.banner("Sistema de Login - EurekaBank");
            }
        }
        return null;
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
            // Ignorar excepciones
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
