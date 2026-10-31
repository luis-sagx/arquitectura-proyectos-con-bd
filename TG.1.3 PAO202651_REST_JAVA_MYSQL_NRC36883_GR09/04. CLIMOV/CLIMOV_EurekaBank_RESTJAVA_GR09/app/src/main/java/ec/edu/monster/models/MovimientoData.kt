package ec.edu.monster.models

import java.math.BigDecimal

data class MovimientoData(
    var codigoCuenta: String = "",
    var numero: Int = 0,
    var ordenCreacion: Long = 0,
    var fecha: String = "",
    var tipo: String = "",
    var importe: BigDecimal = BigDecimal.ZERO,
    var referencia: String? = null,
    var saldoActual: BigDecimal = BigDecimal.ZERO,
    var nombreCliente: String = ""
)
