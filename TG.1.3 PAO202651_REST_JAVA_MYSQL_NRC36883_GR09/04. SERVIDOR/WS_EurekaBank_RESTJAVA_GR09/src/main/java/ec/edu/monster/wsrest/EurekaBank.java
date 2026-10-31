package ec.edu.monster.wsrest;

import ec.edu.monster.model.DatosCuenta;
import ec.edu.monster.model.MovimientoData;
import ec.edu.monster.model.MovimientoRequestDto;
import ec.edu.monster.model.ResultadoOperacion;
import ec.edu.monster.service.EurekaService;
import ec.edu.monster.service.Login;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

@Path("eurekabank")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EurekaBank {

    private static final String DEFAULT_COD_EMPLEADO = "0001";
    private static final List<String> TIPOS_PERMITIDOS = Arrays.asList(
            "DEPOSITO", "RETIRO", "TRANSFERENCIA");

    private final EurekaService eurekaService = new EurekaService();
    private final Login loginService = new Login();

    @GET
    @Path("cuentas")
    public Response traerCuentasConClientes() {
        try {
            return Response.ok(eurekaService.leerCuentasConClientes()).build();
        } catch (Exception e) {
            return error(500, "Error al obtener cuentas");
        }
    }

    @GET
    @Path("movimientos")
    public Response traerMovimientos(@QueryParam("cuenta") String cuenta) {
        try {
            List<MovimientoData> movimientos = eurekaService.leerMovimientos(cuenta);
            return Response.ok(movimientos).build();
        } catch (Exception e) {
            return error(500, "Error al obtener movimientos");
        }
    }

    @POST
    @Path("movimientos")
    public Response registrarMovimiento(MovimientoRequestDto request) {
        if (request == null || request.getCuentaOrigen() == null
                || request.getCuentaOrigen().isBlank()) {
            return error(400, "Cuenta origen es requerida");
        }
        if (!Double.isFinite(request.getImporte()) || request.getImporte() <= 0) {
            return error(400, "El importe debe ser mayor a 0");
        }
        if (request.getTipo() == null || request.getTipo().isBlank()) {
            return error(400, "Tipo de movimiento es requerido");
        }

        String tipo = request.getTipo().trim().toUpperCase(java.util.Locale.ROOT);
        try {
            switch (tipo) {
                case "DEPOSITO" -> {
                    if (hasValue(request.getCuentaDestino())) {
                        return error(400, "Depósito no debe tener cuenta destino");
                    }
                    eurekaService.registrarDeposito(request.getCuentaOrigen(), request.getImporte(), DEFAULT_COD_EMPLEADO);
                }
                case "RETIRO" -> {
                    if (hasValue(request.getCuentaDestino())) {
                        return error(400, "Retiro no debe tener cuenta destino");
                    }
                    eurekaService.registrarRetiro(request.getCuentaOrigen(), request.getImporte(), DEFAULT_COD_EMPLEADO);
                }
                case "TRANSFERENCIA" -> {
                    if (!hasValue(request.getCuentaDestino())) {
                        return error(400, "Cuenta destino es requerida para transferencia");
                    }
                    eurekaService.registrarTransferencia(request.getCuentaOrigen(), request.getCuentaDestino(),
                            request.getImporte(), DEFAULT_COD_EMPLEADO);
                }
                default -> {
                    return error(400, "Tipo de movimiento no válido: " + request.getTipo());
                }
            }
            return Response.ok(ResultadoOperacion.exito()).build();
        } catch (RuntimeException e) {
            return error(400, e.getMessage());
        }
    }

    @POST
    @Path("login")
    public Response iniciarSesion(LoginRequest request) {
        if (request == null || !hasValue(request.usuario) || !hasValue(request.contrasena)) {
            return error(400, "Usuario y contraseña son requeridos");
        }
        try {
            boolean valido = loginService.IniciarSesion(request.usuario, request.contrasena);
            ResultadoOperacion resultado = valido
                    ? new ResultadoOperacion(1, "Login exitoso")
                    : ResultadoOperacion.error("Usuario o contraseña incorrectos");
            return Response.ok(resultado).build();
        } catch (RuntimeException e) {
            return error(500, "Error al validar credenciales");
        }
    }

    @GET
    @Path("tipos")
    public List<String> obtenerTiposPermitidos() {
        return TIPOS_PERMITIDOS;
    }

    public static class LoginRequest {
        public String usuario;
        public String contrasena;
    }

    private static boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }

    private static Response error(int status, String mensaje) {
        return Response.status(status)
                .entity(ResultadoOperacion.error(mensaje == null ? "Error interno del servidor" : mensaje))
                .build();
    }
}
