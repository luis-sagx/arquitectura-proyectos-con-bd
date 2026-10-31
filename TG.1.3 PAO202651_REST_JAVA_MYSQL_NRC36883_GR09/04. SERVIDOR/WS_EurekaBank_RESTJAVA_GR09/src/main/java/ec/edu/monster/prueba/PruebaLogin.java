/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.prueba;

import ec.edu.monster.service.Login;

/**
 *
 * @author leona
 */
public class PruebaLogin {

    public static void main(String[] args) {
        try {
            //dato de la prueba
            String usuario = "Monster";
            String contrasena = "Monster9";
            //proceso
            Login service = new Login();
            if (service.IniciarSesion(usuario, contrasena)) {
                System.out.println("Logeado exitosamente");
            } else {
                System.out.println("Credenciales incorrectas");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
