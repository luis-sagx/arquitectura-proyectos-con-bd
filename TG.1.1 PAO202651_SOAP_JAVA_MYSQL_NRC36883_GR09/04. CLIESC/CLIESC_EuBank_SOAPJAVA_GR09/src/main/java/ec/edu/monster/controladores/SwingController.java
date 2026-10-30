package ec.edu.monster.controladores;

import ec.edu.monster.servicios.EuBankService;
import ec.edu.monster.ws.eurekabank.DatosCuenta;
import ec.edu.monster.ws.eurekabank.MovimientoData;
import ec.edu.monster.ws.eurekabank.ResultadoOperacion;
import java.util.List;

/** Coordinates Swing views with the SOAP application service. */
public class SwingController {

    private final EuBankService service;

    public SwingController(EuBankService service) {
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

    public ResultadoOperacion registrarMovimiento(String tipo, String origen, String destino, double importe) {
        return service.regMovimiento(tipo, origen, destino, importe);
    }
}
