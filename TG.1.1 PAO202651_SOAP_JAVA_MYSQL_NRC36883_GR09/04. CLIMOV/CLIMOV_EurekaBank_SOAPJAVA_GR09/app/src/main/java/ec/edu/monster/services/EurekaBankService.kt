// app/src/main/java/ec/edu/monster/services/EurekaBankService.kt

package ec.edu.monster.services

import android.util.Log
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import android.os.Handler
import android.os.Looper
import ec.edu.monster.models.DatosCuenta
import ec.edu.monster.models.MovimientoData
import ec.edu.monster.models.ResultadoOperacion
import ec.edu.monster.BuildConfig
import org.ksoap2.serialization.SoapPrimitive
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Locale

object EurekaBankService {
    private const val TAG = "EurekaBankService"
    private const val NAMESPACE = "http://ws.monster.edu.ec/"
    private const val URL = BuildConfig.SOAP_URL
    private const val METHOD_LOGIN = "iniciarSesion"
    private const val METHOD_CUENTAS = "traerCuentasConClientes"
    private const val METHOD_MOVIMIENTOS = "traerMovimientos"
    private const val METHOD_REG_MOVIMIENTO = "regMovimiento"
    // Hilo principal
    private val mainHandler = Handler(Looper.getMainLooper())

    fun iniciarSesion(
        usuario: String,
        contrasena: String,
        callback: (success: Boolean, message: String?) -> Unit
    ) {
        Thread {
            try {
                Log.d(TAG, "Iniciando sesión para usuario: $usuario")

                val request = SoapObject(NAMESPACE, METHOD_LOGIN).apply {
                    addProperty("usuario", usuario)
                    addProperty("contrasena", contrasena)
                }

                val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
                    dotNet = false
                    setOutputSoapObject(request)
                }

                val transport = HttpTransportSE(URL).apply {
                    debug = true
                }

                val soapAction = "$NAMESPACE$METHOD_LOGIN"
                transport.call(soapAction, envelope)

                Log.d(TAG, "=== SOAP REQUEST DUMP ===")
                Log.d(TAG, transport.requestDump ?: "No request dump")
                Log.d(TAG, "=== SOAP RESPONSE DUMP ===")
                Log.d(TAG, transport.responseDump ?: "No response dump")

                val responseBody = envelope.bodyIn as? SoapObject
                val resultadoStr = try {
                    responseBody?.getProperty("resultado")?.toString()
                } catch (e: Exception) {
                    "null"
                }

                Log.d(TAG, "Valor de <resultado>: '$resultadoStr'")

                val result = resultadoStr?.trim().equals("true", ignoreCase = true) == true

                // LLAMAR CALLBACK EN HILO PRINCIPAL
                mainHandler.post {
                    callback(result, if (result) "Login exitoso" else "Credenciales incorrectas")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error en SOAP", e)
                mainHandler.post {
                    callback(false, "Error: ${e.message}")
                }
            }
        }.start()
    }

    fun traerCuentasConClientes(
        callback: (List<DatosCuenta>, String?) -> Unit
    ) {
        Thread {
            try {
                val request = SoapObject(NAMESPACE, METHOD_CUENTAS)

                val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
                    dotNet = false
                    setOutputSoapObject(request)
                }

                val transport = HttpTransportSE(URL).apply { debug = true }
                transport.call("$NAMESPACE$METHOD_CUENTAS", envelope)

                Log.d(TAG, "Cuentas Response: ${transport.responseDump}")

                val body = envelope.bodyIn as? SoapObject
                if (body == null) {
                    mainHandler.post { callback(emptyList(), "Sin respuesta") }
                    return@Thread
                }

                val cuentas = mutableListOf<DatosCuenta>()

                // ITERAR LAS 5 CUENTAS DIRECTAMENTE
                for (i in 0 until body.propertyCount) {
                    val cuentaObj = body.getProperty(i) as? SoapObject
                    if (cuentaObj != null) {
                        val cuenta = DatosCuenta().apply {
                            codigo = cuentaObj.getPropertySafelyAsString("Codigo") ?: ""
                            emailCliente = cuentaObj.getPropertySafelyAsString("EmailCliente") ?: ""
                            estado = cuentaObj.getPropertySafelyAsString("Estado") ?: ""
                            moneda = cuentaObj.getPropertySafelyAsString("Moneda") ?: ""
                            nombreCliente = cuentaObj.getPropertySafelyAsString("NombreCliente") ?: ""
                            saldo = cuentaObj.getPropertySafelyAsString("Saldo")?.let { BigDecimal(it) } ?: BigDecimal.ZERO
                            telefonoCliente = cuentaObj.getPropertySafelyAsString("TelefonoCliente") ?: ""
                        }
                        cuentas.add(cuenta)
                    }
                }

                Log.d(TAG, "Cuentas parseadas: ${cuentas.size}")

                mainHandler.post {
                    callback(cuentas, null)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error al traer cuentas", e)
                mainHandler.post {
                    callback(emptyList(), "Error: ${e.message}")
                }
            }
        }.start()
    }

    fun traerMovimientos(
        cuenta: String,
        callback: (List<MovimientoData>, String?) -> Unit
    ) {
        Thread {
            try {
                val request = SoapObject(NAMESPACE, METHOD_MOVIMIENTOS).apply {
                    addProperty("cuenta", cuenta)
                }

                val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
                    dotNet = false
                    setOutputSoapObject(request)
                }

                val transport = HttpTransportSE(URL).apply { debug = true }
                transport.call("$NAMESPACE$METHOD_MOVIMIENTOS", envelope)

                Log.d(TAG, "Movimientos Response: ${transport.responseDump}")

                val body = envelope.bodyIn as? SoapObject
                if (body == null) {
                    mainHandler.post { callback(emptyList(), "Sin respuesta") }
                    return@Thread
                }

                val movimientos = mutableListOf<MovimientoData>()

                // MISMA LÓGICA QUE CUENTAS: body.propertyCount = número de movimientos
                for (i in 0 until body.propertyCount) {
                    val movObj = body.getProperty(i) as? SoapObject
                    if (movObj != null) {
                        val mov = MovimientoData().apply {
                            codigoCuenta = movObj.getPropertySafelyAsString("CodigoCuenta") ?: ""
                            numero = movObj.getPropertySafelyAsString("Numero")?.toIntOrNull() ?: 0
                            fecha = try {
                                val dateStr = movObj.getPropertySafelyAsString("Fecha")
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
                            } catch (e: Exception) { null }
                            tipo = movObj.getPropertySafelyAsString("Tipo") ?: ""
                            importe = movObj.getPropertySafelyAsString("Importe")?.let { BigDecimal(it) } ?: BigDecimal.ZERO
                            referencia = movObj.getPropertySafelyAsString("Referencia")
                            saldoActual = movObj.getPropertySafelyAsString("SaldoActual")?.let { BigDecimal(it) } ?: BigDecimal.ZERO
                            nombreCliente = movObj.getPropertySafelyAsString("NombreCliente") ?: ""
                        }
                        movimientos.add(mov)
                    }
                }

                Log.d(TAG, "Movimientos parseados: ${movimientos.size}")
                mainHandler.post { callback(movimientos, null) }

            } catch (e: Exception) {
                Log.e(TAG, "Error al traer movimientos", e)
                mainHandler.post { callback(emptyList(), "Error: ${e.message}") }
            }
        }.start()
    }

    fun regMovimiento(
        cuentaOrigen: String,
        cuentaDestino: String?,
        tipo: String,
        importe: Double,
        callback: (ResultadoOperacion?, String?) -> Unit
    ) {
        Thread {
            try {
                val request = SoapObject(NAMESPACE, METHOD_REG_MOVIMIENTO).apply {
                    addProperty("cuentaOrigen", cuentaOrigen)
                    if (cuentaDestino != null) addProperty("cuentaDestino", cuentaDestino)
                    addProperty("tipo", tipo)
                    // CORREGIDO: String en vez de Double
                    addProperty("importe", importe.toString())
                }

                val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
                    dotNet = false
                    setOutputSoapObject(request)
                }

                val transport = HttpTransportSE(URL).apply { debug = true }
                transport.call("$NAMESPACE$METHOD_REG_MOVIMIENTO", envelope)

                Log.d(TAG, "RegMovimiento Response: ${transport.responseDump}")

                val body = envelope.bodyIn as? SoapObject
                if (body == null) {
                    mainHandler.post { callback(null, "Sin respuesta") }
                    return@Thread
                }

                val resultadoObj = body.getPropertySafely("resultado") as? SoapObject
                val resultado = if (resultadoObj != null) {
                    ResultadoOperacion(
                        codigo = resultadoObj.getPropertySafelyAsString("codigo")?.toIntOrNull() ?: 0,
                        mensaje = resultadoObj.getPropertySafelyAsString("mensaje") ?: "Sin mensaje"
                    )
                } else {
                    ResultadoOperacion(-1, "Formato inválido")
                }

                mainHandler.post { callback(resultado, null) }

            } catch (e: Exception) {
                Log.e(TAG, "Error al registrar movimiento", e)
                mainHandler.post { callback(null, e.message) }
            }
        }.start()
    }
    private const val METHOD_TIPOS = "tipoMovimientosPermitidos"

    fun obtenerTiposMovimiento(
        callback: (List<String>, String?) -> Unit
    ) {
        Thread {
            try {
                val request = SoapObject(NAMESPACE, METHOD_TIPOS)
                val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
                    dotNet = false
                    setOutputSoapObject(request)
                }

                val transport = HttpTransportSE(URL).apply { debug = true }
                transport.call("$NAMESPACE$METHOD_TIPOS", envelope)

                val body = envelope.bodyIn as? SoapObject
                if (body == null) {
                    mainHandler.post { callback(emptyList(), "Sin respuesta") }
                    return@Thread
                }

                val tipos = mutableListOf<String>()
                for (i in 0 until body.propertyCount) {
                    val item = body.getProperty(i)
                    if (item is SoapPrimitive) {
                        tipos.add(item.toString())
                    }
                }

                mainHandler.post { callback(tipos, null) }

            } catch (e: Exception) {
                mainHandler.post { callback(emptyList(), e.message) }
            }
        }.start()
    }
    private fun Any.getPropertySafely(name: String): Any? {
        return try {
            (this as? SoapObject)?.getProperty(name)
        } catch (e: Exception) {
            null
        }
    }

    private fun Any.getPropertySafelyAsString(name: String): String? {
        return getPropertySafely(name)?.toString()
    }

}
