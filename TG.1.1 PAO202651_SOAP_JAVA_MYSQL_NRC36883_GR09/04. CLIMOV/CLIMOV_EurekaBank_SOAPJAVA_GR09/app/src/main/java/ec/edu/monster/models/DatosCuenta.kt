
package ec.edu.monster.models

import org.ksoap2.serialization.KvmSerializable
import org.ksoap2.serialization.PropertyInfo
import java.math.BigDecimal
import java.util.Hashtable

data class DatosCuenta(
    var codigo: String = "",
    var nombreCliente: String = "",
    var saldo: BigDecimal = BigDecimal.ZERO,
    var estado: String = "",
    var moneda: String = "",
    var emailCliente: String = "",
    var telefonoCliente: String = ""
) : KvmSerializable {

    override fun getProperty(index: Int): Any = when (index) {
        0 -> codigo
        1 -> nombreCliente
        2 -> saldo
        3 -> estado
        4 -> moneda
        5 -> emailCliente
        6 -> telefonoCliente
        else -> throw IndexOutOfBoundsException()
    }

    override fun getPropertyCount(): Int = 7

    override fun setProperty(index: Int, value: Any) {
        when (index) {
            0 -> codigo = value.toString()
            1 -> nombreCliente = value.toString()
            2 -> saldo = if (value is BigDecimal) value else BigDecimal(value.toString())
            3 -> estado = value.toString()
            4 -> moneda = value.toString()
            5 -> emailCliente = value.toString()
            6 -> telefonoCliente = value.toString()
            else -> throw IndexOutOfBoundsException()
        }
    }

    override fun getPropertyInfo(index: Int, properties: Hashtable<*, *>?, info: PropertyInfo) {
        when (index) {
            0 -> { info.name = "Codigo"; info.type = PropertyInfo.STRING_CLASS }
            1 -> { info.name = "NombreCliente"; info.type = PropertyInfo.STRING_CLASS }
            2 -> { info.name = "Saldo"; info.type = BigDecimal::class.java }
            3 -> { info.name = "Estado"; info.type = PropertyInfo.STRING_CLASS }
            4 -> { info.name = "Moneda"; info.type = PropertyInfo.STRING_CLASS }
            5 -> { info.name = "EmailCliente"; info.type = PropertyInfo.STRING_CLASS }
            6 -> { info.name = "TelefonoCliente"; info.type = PropertyInfo.STRING_CLASS }
        }
    }
}