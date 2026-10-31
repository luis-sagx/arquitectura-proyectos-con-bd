package ec.edu.monster.ui.viewmodels

import androidx.lifecycle.ViewModel
import ec.edu.monster.models.MovimientoData
import ec.edu.monster.services.EurekaBankService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MovementsViewModel : ViewModel() {
    private val _movimientos = MutableStateFlow<List<MovimientoData>>(emptyList())
    val movimientos: StateFlow<List<MovimientoData>> = _movimientos.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val _tipos = MutableStateFlow<List<String>>(emptyList())
    val tipos: StateFlow<List<String>> = _tipos.asStateFlow()
    private val _operationLoading = MutableStateFlow(false)
    val operationLoading: StateFlow<Boolean> = _operationLoading.asStateFlow()
    private val _operationError = MutableStateFlow<String?>(null)
    val operationError: StateFlow<String?> = _operationError.asStateFlow()

    fun loadMovimientos(cuenta: String) {
        _isLoading.value = true
        _error.value = null
        EurekaBankService.traerMovimientos(cuenta) { result, error ->
            _movimientos.value = result
            _error.value = error
            _isLoading.value = false
        }
    }

    fun loadTiposMovimiento() {
        EurekaBankService.obtenerTiposMovimiento { result, error ->
            _tipos.value = result
            if (error != null) _operationError.value = error
        }
    }

    fun registrarMovimiento(
        cuentaOrigen: String,
        cuentaDestino: String?,
        tipo: String,
        importe: Double,
        onSuccess: () -> Unit
    ) {
        _operationLoading.value = true
        _operationError.value = null
        EurekaBankService.regMovimiento(cuentaOrigen, cuentaDestino, tipo, importe) { result, error ->
            _operationLoading.value = false
            val mensajeError = error ?: result?.takeUnless { it.esExito() }?.mensaje
            _operationError.value = mensajeError
            if (mensajeError == null && result != null) onSuccess()
        }
    }
}
