package ec.edu.monster.ui.viewmodels

import androidx.lifecycle.ViewModel
import ec.edu.monster.services.EurekaBankService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginUiState(val loading: Boolean = false, val message: String? = null)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun iniciarSesion(usuario: String, contrasena: String, onResult: (Boolean, String?) -> Unit) {
        _uiState.value = LoginUiState(loading = true)
        EurekaBankService.iniciarSesion(usuario, contrasena) { success, message ->
            _uiState.value = LoginUiState(message = message)
            onResult(success, message)
        }
    }
}
