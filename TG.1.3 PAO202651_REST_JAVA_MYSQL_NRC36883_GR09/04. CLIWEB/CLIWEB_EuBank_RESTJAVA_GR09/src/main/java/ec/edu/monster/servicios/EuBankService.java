package ec.edu.monster.servicios;

import ec.edu.monster.model.DatosCuenta;
import ec.edu.monster.model.MovimientoData;
import ec.edu.monster.model.MovimientoRequestDto;
import ec.edu.monster.model.ResultadoOperacion;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EuBankService {
    private final RestTemplate restTemplate;
    private final String apiUrl;

    public EuBankService(@Value("${eubank.rest.url}") String apiUrl) {
        this.apiUrl = apiUrl.replaceAll("/$", "");
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        this.restTemplate = new RestTemplate(factory);
    }

    public boolean iniciarSesion(String usuario, String contrasena) {
        ResultadoOperacion resultado = restTemplate.postForObject(apiUrl + "/login",
                new LoginRequest(usuario, contrasena), ResultadoOperacion.class);
        return resultado != null && resultado.getCodigo() == 1;
    }

    public List<DatosCuenta> traerCuentasConClientes() {
        DatosCuenta[] response = restTemplate.getForObject(apiUrl + "/cuentas", DatosCuenta[].class);
        return Arrays.asList(response == null ? new DatosCuenta[0] : response);
    }

    public List<MovimientoData> traerMovimientos(String cuenta) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/movimientos");
        if (cuenta != null && !cuenta.isBlank()) {
            builder.queryParam("cuenta", cuenta);
        }
        MovimientoData[] response = restTemplate.getForObject(builder.build().encode().toUri(), MovimientoData[].class);
        return Arrays.asList(response == null ? new MovimientoData[0] : response);
    }

    public List<String> obtenerTipoMovimientosPermitidos() {
        String[] response = restTemplate.getForObject(apiUrl + "/tipos", String[].class);
        return Arrays.asList(response == null ? new String[0] : response);
    }

    public ResultadoOperacion regMovimiento(String tipo, String cuentaOrigen, String cuentaDestino, double importe) {
        MovimientoRequestDto request = new MovimientoRequestDto();
        request.setTipo(tipo);
        request.setCuentaOrigen(cuentaOrigen);
        request.setCuentaDestino(cuentaDestino);
        request.setImporte(importe);
        try {
            return restTemplate.postForObject(apiUrl + "/movimientos", request, ResultadoOperacion.class);
        } catch (RestClientException e) {
            throw new IllegalArgumentException("No se pudo registrar el movimiento: " + e.getMessage(), e);
        }
    }

    public record LoginRequest(String usuario, String contrasena) {
    }
}
