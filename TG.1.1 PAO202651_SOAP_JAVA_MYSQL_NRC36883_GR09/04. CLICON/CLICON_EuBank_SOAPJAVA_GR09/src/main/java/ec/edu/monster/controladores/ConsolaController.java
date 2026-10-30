package ec.edu.monster.controladores;

import ec.edu.monster.servicios.EuBankService;
import ec.edu.monster.ws.eurekabank.DatosCuenta;
import ec.edu.monster.ws.eurekabank.MovimientoData;
import ec.edu.monster.ws.eurekabank.ResultadoOperacion;
import java.util.List;
import org.springframework.stereotype.Component;

/** Coordinates console views with the SOAP application service. */
@Component
public class ConsolaController {

    private final EuBankService service;

    public ConsolaController(EuBankService service) {
        this.service = service;
    }

    public boolean autenticar(String usuario, String contrasena) {
        return service.iniciarSesion(usuario, contrasena);
    }

    public List<DatosCuenta> traerCuentas() {
        return service.traerCuentasConClientes();
    }

    public List<MovimientoData> traerMovimientos(String cuenta) {
        return service.traerMovimientos(cuenta);
    }

    public ResultadoOperacion registrarMovimiento(String tipo, String cuentaOrigen, String cuentaDestino, double importe) {
        return service.regMovimiento(tipo, cuentaOrigen, cuentaDestino, importe);
    }
}
