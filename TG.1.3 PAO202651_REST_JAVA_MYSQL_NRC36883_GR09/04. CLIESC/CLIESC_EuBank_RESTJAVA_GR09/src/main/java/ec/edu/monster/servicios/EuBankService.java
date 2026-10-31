package ec.edu.monster.servicios;

import ec.edu.monster.model.DatosCuenta;
import ec.edu.monster.model.MovimientoData;
import ec.edu.monster.model.MovimientoRequestDto;
import ec.edu.monster.model.ResultadoOperacion;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@org.springframework.stereotype.Service
public class EuBankService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl = System.getenv().getOrDefault("EUBANK_REST_URL",
            "http://localhost:8080/WS_EurekaBank_RESTJAVA_GR09/api/eurekabank");

    public boolean iniciarSesion(String usuario, String contrasena) {
        ResultadoOperacion result = restTemplate.postForObject(apiUrl + "/login",
                new LoginRequest(usuario, contrasena), ResultadoOperacion.class);
        return result != null && result.getCodigo() == 1;
    }

    public List<DatosCuenta> traerCuentasConClientes() {
        DatosCuenta[] result = restTemplate.getForObject(apiUrl + "/cuentas", DatosCuenta[].class);
        return Arrays.asList(result == null ? new DatosCuenta[0] : result);
    }

    public List<MovimientoData> traerMovimientos(String cuenta) {
        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(apiUrl + "/movimientos");
        if (cuenta != null && !cuenta.isBlank()) url.queryParam("cuenta", cuenta);
        MovimientoData[] result = restTemplate.getForObject(url.build().encode().toUri(), MovimientoData[].class);
        return Arrays.asList(result == null ? new MovimientoData[0] : result);
    }

    public List<String> obtenerTipoMovimientosPermitidos() {
        String[] result = restTemplate.getForObject(apiUrl + "/tipos", String[].class);
        return Arrays.asList(result == null ? new String[0] : result);
    }

    public ResultadoOperacion regMovimiento(String tipo, String origen, String destino, double importe) {
        MovimientoRequestDto req = new MovimientoRequestDto();
        req.setTipo(tipo); req.setCuentaOrigen(origen); req.setCuentaDestino(destino); req.setImporte(importe);
        return restTemplate.postForObject(apiUrl + "/movimientos", req, ResultadoOperacion.class);
    }

    public record LoginRequest(String usuario, String contrasena) {}
}
