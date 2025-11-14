package sv.edu.catolica.asistedocente.ui.screens.login

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sv.edu.catolica.asistedocente.data.repository.DocenteRepository
import sv.edu.catolica.asistedocente.utils.AuthHelper
import javax.inject.Inject

/**
 * ViewModel para la pantalla de login
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val docenteRepository: DocenteRepository
) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Actualiza el email
     */
    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    /**
     * Actualiza la contraseña (en este caso usamos el nombre del docente)
     */
    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    /**
     * Intenta hacer login
     * Nota: En esta versión simplificada, usamos el email del docente para autenticar
     */
    fun login() {
        val state = _uiState.value

        // Validaciones
        var hasErrors = false

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "El email es requerido") }
            hasErrors = true
        }

        if (state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "La contraseña es requerida") }
            hasErrors = true
        }

        if (hasErrors) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, generalError = null) }

                // Buscar docente por email
                val docentes = docenteRepository.getAllDocentes().firstOrNull()
                val docente = docentes?.find {
                    it.email.equals(state.email, ignoreCase = true) && it.activo
                }

                if (docente != null) {
                    // Verificar contraseña
                    // NOTA: En producción, usar bcrypt o similar para contraseñas hasheadas
                    if (docente.password == state.password) {
                        // Login exitoso
                        AuthHelper.saveSession(
                            context,
                            docente.id,
                            docente.email,
                            "${docente.nombre} ${docente.apellido}"
                        )
                        _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                generalError = "Credenciales incorrectas"
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalError = "No se encontró un docente con este email"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        generalError = "Error al iniciar sesión: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Resetea el estado de login exitoso
     */
    fun resetLoginSuccess() {
        _uiState.update { it.copy(loginSuccess = false) }
    }
}

/**
 * Estado de la UI de login
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false
)
