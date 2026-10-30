package ec.edu.monster.models

import java.math.BigDecimal
import java.util.*

data class MovimientoData(
    var codigoCuenta: String = "",
    var numero: Int = 0,
    var fecha: Date? = null,
    var tipo: String = "",
    var importe: BigDecimal = BigDecimal.ZERO,
    var referencia: String? = null,
    var saldoActual: BigDecimal = BigDecimal.ZERO,
    var nombreCliente: String = ""
)