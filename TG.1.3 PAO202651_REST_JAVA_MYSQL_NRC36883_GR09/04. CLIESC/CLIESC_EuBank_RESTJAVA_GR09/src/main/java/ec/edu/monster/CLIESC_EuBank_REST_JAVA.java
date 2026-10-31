/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package ec.edu.monster;

import ec.edu.monster.servicios.EuBankService;
import ec.edu.monster.vista.login;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * @author Dome
 */
public class CLIESC_EuBank_REST_JAVA {

    public static void main(String[] args) {
        
        // 1. Preparamos Spring Boot
        ConfigurableApplicationContext context = new SpringApplicationBuilder(CLIESC_EuBank_REST_JAVA.class)
                .headless(false) 
                .run(args);

        java.awt.EventQueue.invokeLater(() -> {
            new login().setVisible(true); 
        });
    }
}
