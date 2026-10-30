package ec.edu.monster.ui.viewmodels

import androidx.lifecycle.ViewModel
import ec.edu.monster.models.DatosCuenta
import ec.edu.monster.services.EurekaBankService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountsViewModel : ViewModel() {
    private val _cuentas = MutableStateFlow<List<DatosCuenta>>(emptyList())
    val cuentas: StateFlow<List<DatosCuenta>> = _cuentas.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadCuentas() {
        _loading.value = true
        _error.value = null
        EurekaBankService.traerCuentasConClientes { result, error ->
            _cuentas.value = result
            _error.value = error
            _loading.value = false
        }
    }
}
