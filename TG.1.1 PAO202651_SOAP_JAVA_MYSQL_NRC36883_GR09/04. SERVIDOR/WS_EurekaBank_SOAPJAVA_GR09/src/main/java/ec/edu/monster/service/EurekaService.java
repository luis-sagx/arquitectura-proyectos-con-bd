package ec.edu.monster.service;

import ec.edu.monster.dao.EurekaRepository;
import ec.edu.monster.model.DatosCuenta;
import ec.edu.monster.model.MovimientoData;
import java.util.List;

/** Application service used by the SOAP controller. */
public class EurekaService {

    private final EurekaRepository repository;

    public EurekaService() {
        this(new EurekaRepository());
    }

    EurekaService(EurekaRepository repository) {
        this.repository = repository;
    }

    public List<DatosCuenta> leerCuentasConClientes() {
        return repository.leerCuentasConClientes();
    }

    public List<DatosCuenta> leerCuentasPorCliente(String clienteCodigo) {
        return repository.leerCuentasPorCliente(clienteCodigo);
    }

    public List<MovimientoData> leerMovimientos(String cuenta) {
        return repository.leerMovimientos(cuenta);
    }

    public void registrarDeposito(String cuenta, double importe, String codEmp) {
        repository.registrarDeposito(cuenta, importe, codEmp);
    }

    public void registrarRetiro(String cuenta, double importe, String codEmp) {
        repository.registrarRetiro(cuenta, importe, codEmp);
    }

    public void registrarTransferencia(String cuentaOrigen, String cuentaDestino, double importe, String codEmp) {
        repository.registrarTransferencia(cuentaOrigen, cuentaDestino, importe, codEmp);
    }

    public void realizarMovimiento(String cuentaOrigen, double importe, String codEmp, String tipo, String cuentaDestino) {
        repository.realizarMovimiento(cuentaOrigen, importe, codEmp, tipo, cuentaDestino);
    }
}
