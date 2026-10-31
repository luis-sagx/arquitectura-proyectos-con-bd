package ec.edu.monster.models

import java.math.BigDecimal

data class DatosCuenta(
    var codigo: String = "",
    var nombreCliente: String = "",
    var saldo: BigDecimal = BigDecimal.ZERO,
    var estado: String = "",
    var moneda: String = "",
    var emailCliente: String = "",
    var telefonoCliente: String = ""
)
