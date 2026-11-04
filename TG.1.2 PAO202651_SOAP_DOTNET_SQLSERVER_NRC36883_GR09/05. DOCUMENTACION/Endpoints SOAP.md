# Endpoints SOAP

El servicio usa SOAP 1.1 sobre HTTP en `http://localhost:9091`.

| Servicio | Endpoint | Operaciones |
|---|---|---|
| Autenticación | `/Services/LoginService.svc` | `Login(usuario, clave)` |
| Cuentas y movimientos | `/Services/MovimientosService.svc` | `GetDatosCuentas`, `GetDatosCuenta(cuenta)`, `GetMovimientos(cuenta)`, `GetTiposMovimiento`, `ProcesarMovimiento(tipo, cuentaOrigen, cuentaDestino, importe)` |

Agrega `?wsdl` a cualquiera de los endpoints para consultar el contrato XML. Ambos servicios usan el espacio de nombres `http://monster.edu.ec/eurekabank/` y contratos de datos versionados en el servidor.
