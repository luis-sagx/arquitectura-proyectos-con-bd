package ec.edu.monster.servicios;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author joela
 */
import ec.edu.monster.ws.eurekabank.DatosCuenta;
import ec.edu.monster.ws.eurekabank.MovimientoData;
import ec.edu.monster.ws.eurekabank.ResultadoOperacion;
import ec.edu.monster.ws.eurekabank.WSEurekaBank;
import ec.edu.monster.ws.eurekabank.WSEurekaBank_Service;
import jakarta.xml.ws.BindingProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EuBankService {
    private final WSEurekaBank port;

    public EuBankService(@Value("${eubank.soap.url}") String endpoint) {
        WSEurekaBank_Service service = new WSEurekaBank_Service();
        this.port = service.getWSEurekaBankPort();
        ((BindingProvider) this.port).getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                endpoint
        );
    }
    
    // 1. Login (Asumiendo que el login está en el mismo WSDL)
    public boolean iniciarSesion(String usuario, String contrasena) {
        // El nombre "iniciarSesion" debe coincidir con el generado
        return port.iniciarSesion(usuario, contrasena);
    }

    // 2. Traer Cuentas
    public List<DatosCuenta> traerCuentasConClientes() {
        return port.traerCuentasConClientes();
    }
    
    // 3. Traer Movimientos
    public List<MovimientoData> traerMovimientos(String cuenta) {
        return port.traerMovimientos(cuenta);
    }
    
    // 4. Traer Tipos de Movimiento
    public List<String> obtenerTipoMovimientosPermitidos() {
        return port.tipoMovimientosPermitidos();
    }
    
    // 5. Registrar Movimiento
    public ResultadoOperacion regMovimiento(String tipo, String cuentaOrigen, String cuentaDestino, double importe) {
        // El "regMovimiento" debe coincidir con el generado
        return port.regMovimiento(cuentaOrigen, cuentaDestino, tipo, importe);
    }
}
