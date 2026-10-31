# API EurekaBank REST

Base local: `http://localhost:8080/WS_EurekaBank_RESTJAVA_GR09/api/eurekabank`

Todos los cuerpos y respuestas usan JSON con UTF-8.

## Login

`POST /login`

```json
{"usuario":"Monster","contrasena":"Monster9"}
```

Respuesta: `ResultadoOperacion` (`codigo`, `mensaje`). Un login correcto devuelve código `1`; credenciales inválidas devuelven código `-1`.

## Cuentas

`GET /cuentas`

Devuelve un arreglo de cuentas activas con código, moneda, saldo, estado y datos de cliente.

## Movimientos

`GET /movimientos?cuenta=00100001`

El parámetro `cuenta` es opcional. Si se omite, se devuelven movimientos de todas las cuentas, como requiere el tablero actual.

`POST /movimientos`

El tablero web ordena los movimientos por fecha descendente y, dentro del mismo dia, por `ordenCreacion` descendente. En bases existentes, aplicar una sola vez `03. DBB/mysql/migrations/20261031-movimiento-orden-global.sql`.

Depósito:

```json
{"cuentaOrigen":"00100001","tipo":"DEPOSITO","importe":25.00}
```

Retiro:

```json
{"cuentaOrigen":"00100001","tipo":"RETIRO","importe":10.00}
```

Transferencia:

```json
{"cuentaOrigen":"00100001","cuentaDestino":"00100002","tipo":"TRANSFERENCIA","importe":10.00}
```

El importe debe ser positivo. Las transferencias se registran dentro de la transacción JDBC del servidor.

## Tipos

`GET /tipos`

Devuelve `DEPOSITO`, `RETIRO` y `TRANSFERENCIA`.

## Códigos HTTP

- `200`: consulta o movimiento exitoso; login devuelve un resultado de negocio en el cuerpo.
- `400`: datos inválidos o error de negocio al registrar movimiento.
- `500`: error al consultar datos o error interno.

Esta API reproduce el modelo de autenticación del ejemplo académico y actualmente no emite token de sesión; debe usarse solo en el entorno local de demostración descrito en el README.
