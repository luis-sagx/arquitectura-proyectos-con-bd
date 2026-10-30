package ec.edu.monster.controladores;

import ec.edu.monster.servicios.EuBankService;
import ec.edu.monster.ws.eurekabank.MovimientoData;
import ec.edu.monster.ws.eurekabank.ResultadoOperacion;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/** MVC controller for dashboard navigation and AJAX endpoints. */
@Controller
public class DashboardController {

    private final EuBankService service;

    public DashboardController(EuBankService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "dashboard/index";
    }

    @GetMapping("/dashboard/get-cuentas")
    @ResponseBody
    public ResponseEntity<?> cuentas() {
        try {
            return ResponseEntity.ok(service.traerCuentasConClientes());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/dashboard/get-movimientos")
    @ResponseBody
    public ResponseEntity<?> movimientos(@RequestParam String cuenta) {
        try {
            return ResponseEntity.ok(service.traerMovimientos(cuenta));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/dashboard/get-todos-movimientos")
    @ResponseBody
    public ResponseEntity<?> todosLosMovimientos() {
        try {
            return ResponseEntity.ok(service.traerMovimientos(""));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/dashboard/get-tipos-movimiento")
    @ResponseBody
    public ResponseEntity<?> tiposDeMovimiento() {
        try {
            return ResponseEntity.ok(service.obtenerTipoMovimientosPermitidos());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/dashboard/crear-movimiento")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crearMovimiento(@RequestBody MovimientoForm form) {
        if (form == null || form.importe() == null || form.importe() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "El importe debe ser mayor a cero."));
        }
        try {
            ResultadoOperacion result = service.regMovimiento(
                    form.tipo(), form.cuentaOrigen(), form.cuentaDestino(), form.importe()
            );
            return ResponseEntity.ok(Map.of(
                    "success", result.getCodigo() == 1,
                    "message", result.getMensaje() == null ? "" : result.getMensaje()
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    public record MovimientoForm(String tipo, String cuentaOrigen, String cuentaDestino, Double importe) {
    }
}
