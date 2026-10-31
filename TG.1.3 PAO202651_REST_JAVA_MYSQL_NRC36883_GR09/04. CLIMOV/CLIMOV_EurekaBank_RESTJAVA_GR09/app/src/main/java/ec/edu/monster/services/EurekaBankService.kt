package ec.edu.monster.services

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ec.edu.monster.BuildConfig
import ec.edu.monster.models.DatosCuenta
import ec.edu.monster.models.MovimientoData
import ec.edu.monster.models.ResultadoOperacion
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

object EurekaBankService {
    private const val TAG = "EurekaBankService"
    private const val API_URL = BuildConfig.REST_API_URL
    private val mainHandler = Handler(Looper.getMainLooper())
    private val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

    fun iniciarSesion(usuario: String, contrasena: String, callback: (Boolean, String?) -> Unit) {
        runRequest(false, callback) {
            val response = post("/login", mapOf("usuario" to usuario, "contrasena" to contrasena))
            val result = gson.fromJson(response, ResultadoOperacion::class.java)
            result.codigo == 1 to if (result.codigo == 1) "Login exitoso" else "Credenciales incorrectas"
        }
    }

    fun traerCuentasConClientes(callback: (List<DatosCuenta>, String?) -> Unit) {
        runRequest(emptyList(), callback) {
            val type = object : TypeToken<List<DatosCuenta>>() {}.type
            gson.fromJson<List<DatosCuenta>>(get("/cuentas"), type) to null
        }
    }

    fun traerMovimientos(cuenta: String, callback: (List<MovimientoData>, String?) -> Unit) {
        runRequest(emptyList(), callback) {
            val query = if (cuenta.isBlank()) "" else "?cuenta=${URLEncoder.encode(cuenta, Charsets.UTF_8.name())}"
            val type = object : TypeToken<List<MovimientoData>>() {}.type
            gson.fromJson<List<MovimientoData>>(get("/movimientos$query"), type) to null
        }
    }

    fun regMovimiento(
        cuentaOrigen: String,
        cuentaDestino: String?,
        tipo: String,
        importe: Double,
        callback: (ResultadoOperacion?, String?) -> Unit
    ) {
        runRequest(null, callback) {
            val body = mutableMapOf<String, Any>(
                "cuentaOrigen" to cuentaOrigen,
                "tipo" to tipo,
                "importe" to importe
            )
            if (!cuentaDestino.isNullOrBlank()) body["cuentaDestino"] = cuentaDestino
            gson.fromJson(post("/movimientos", body), ResultadoOperacion::class.java) to null
        }
    }

    fun obtenerTiposMovimiento(callback: (List<String>, String?) -> Unit) {
        runRequest(emptyList(), callback) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson<List<String>>(get("/tipos"), type) to null
        }
    }

    private fun get(path: String): String = request("GET", path, null)

    private fun post(path: String, body: Any): String = request("POST", path, gson.toJson(body))

    private fun request(method: String, path: String, body: String?): String {
        val connection = (URL(API_URL.trimEnd('/') + path).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 8000
            readTimeout = 12000
            setRequestProperty("Accept", "application/json")
            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            }
        }
        return try {
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val response = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (status !in 200..299) error("HTTP $status: $response")
            response
        } finally {
            connection.disconnect()
        }
    }

    private fun <T> runRequest(fallback: T, callback: (T, String?) -> Unit, action: () -> Pair<T, String?>) {
        Thread {
            try {
                val (result, message) = action()
                mainHandler.post { callback(result, message) }
            } catch (e: Exception) {
                Log.e(TAG, "REST request failed", e)
                mainHandler.post {
                    callback(fallback, e.message ?: "Error de conexión con el servicio REST")
                }
            }
        }.start()
    }
}
