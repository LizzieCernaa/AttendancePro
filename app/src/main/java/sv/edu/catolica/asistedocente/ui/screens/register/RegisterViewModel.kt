package sv.edu.catolica.asistedocente.ui.screens.register

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sv.edu.catolica.asistedocente.data.local.entities.Docente
import sv.edu.catolica.asistedocente.data.repository.DocenteRepository
import sv.edu.catolica.asistedocente.utils.AuthHelper
import javax.inject.Inject

/**
 * ViewModel para el registro de nuevos docentes
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    application: Application,
    private val docenteRepository: DocenteRepository
) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /**
     * Actualiza el nombre
     */
    fun onNombreChange(nombre: String) {
        _uiState.update {
            it.copy(
                nombre = nombre,
                nombreError = null
            )
        }
    }

    /**
     * Actualiza el apellido
     */
    fun onApellidoChange(apellido: String) {
        _uiState.update {
            it.copy(
                apellido = apellido,
                apellidoError = null
            )
        }
    }

    /**
     * Actualiza el email
     */
    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null
            )
        }
    }

    /**
     * Actualiza el teléfono
     */
    fun onTelefonoChange(telefono: String) {
        _uiState.update {
            it.copy(telefono = telefono)
        }
    }

    /**
     * Actualiza la contraseña
     */
    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = null
            )
        }
    }

    /**
     * Actualiza la confirmación de contraseña
     */
    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = null
            )
        }
    }

    /**
     * Registra un nuevo docente
     */
    fun register() {
        val state = _uiState.value

        // Validaciones
        var hasErrors = false

        if (state.nombre.isBlank()) {
            _uiState.update { it.copy(nombreError = "El nombre es requerido") }
            hasErrors = true
        }

        if (state.apellido.isBlank()) {
            _uiState.update { it.copy(apellidoError = "El apellido es requerido") }
            hasErrors = true
        }

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "El email es requerido") }
            hasErrors = true
        } else if (!isValidEmail(state.email)) {
            _uiState.update { it.copy(emailError = "Email inválido") }
            hasErrors = true
        }

        if (state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "La contraseña es requerida") }
            hasErrors = true
        } else if (state.password.length < 4) {
            _uiState.update { it.copy(passwordError = "La contraseña debe tener al menos 4 caracteres") }
            hasErrors = true
        }

        if (state.confirmPassword.isBlank()) {
            _uiState.update { it.copy(confirmPasswordError = "Confirme su contraseña") }
            hasErrors = true
        } else if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Las contraseñas no coinciden") }
            hasErrors = true
        }

        if (hasErrors) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, generalError = null) }

                // Verificar que el email no esté registrado
                val existingDocentes = docenteRepository.getAllDocentes()
                existingDocentes.collect { docentes ->
                    val emailExists = docentes.any {
                        it.email.equals(state.email, ignoreCase = true)
                    }

                    if (emailExists) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                emailError = "Este email ya está registrado"
                            )
                        }
                        return@collect
                    }

                    // Crear nuevo docente
                    val nuevoDocente = Docente(
                        nombre = state.nombre.trim(),
                        apellido = state.apellido.trim(),
                        email = state.email.trim().lowercase(),
                        password = state.password,
                        telefono = state.telefono.trim().ifBlank { null },
                        foto = null,
                        activo = true,
                        fechaCreacion = System.currentTimeMillis()
                    )

                    val docenteId = docenteRepository.insertDocente(nuevoDocente)

                    // Iniciar sesión automáticamente
                    AuthHelper.saveSession(
                        context,
                        docenteId,
                        nuevoDocente.email,
                        "${nuevoDocente.nombre} ${nuevoDocente.apellido}"
                    )

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            registerSuccess = true
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        generalError = "Error al registrar: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Valida formato de email
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

/**
 * Estado del UI de registro
 */
data class RegisterUiState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nombreError: String? = null,
    val apellidoError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false
)
