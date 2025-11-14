package sv.edu.catolica.asistedocente.ui.screens.students

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sv.edu.catolica.asistedocente.data.local.entities.Estudiante
import sv.edu.catolica.asistedocente.data.repository.EstudianteRepository
import javax.inject.Inject

/**
 * ViewModel para crear y editar estudiantes
 */
@HiltViewModel
class AddEditStudentViewModel @Inject constructor(
    private val estudianteRepository: EstudianteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupId: Long = savedStateHandle.get<Long>("groupId") ?: -1L
    private val studentId: Long = savedStateHandle.get<Long>("studentId") ?: -1L

    private val _uiState = MutableStateFlow(AddEditStudentUiState(groupId = groupId))
    val uiState: StateFlow<AddEditStudentUiState> = _uiState.asStateFlow()

    init {
        if (studentId != -1L) {
            loadEstudiante(studentId)
        }
    }

    /**
     * Carga los datos del estudiante para edición
     */
    private fun loadEstudiante(id: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val estudiante = estudianteRepository.getEstudianteByIdSync(id)
                if (estudiante != null) {
                    _uiState.value = _uiState.value.copy(
                        nombre = estudiante.nombre,
                        apellido = estudiante.apellido,
                        codigo = estudiante.codigo,
                        email = estudiante.email ?: "",
                        isEditMode = true,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Estudiante no encontrado",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Actualiza el nombre
     */
    fun onNombreChange(nombre: String) {
        _uiState.value = _uiState.value.copy(
            nombre = nombre,
            nombreError = null
        )
    }

    /**
     * Actualiza el apellido
     */
    fun onApellidoChange(apellido: String) {
        _uiState.value = _uiState.value.copy(
            apellido = apellido,
            apellidoError = null
        )
    }

    /**
     * Actualiza el código
     */
    fun onCodigoChange(codigo: String) {
        _uiState.value = _uiState.value.copy(
            codigo = codigo,
            codigoError = null
        )
    }

    /**
     * Actualiza el email
     */
    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = null
        )
    }

    /**
     * Valida y guarda el estudiante
     */
    fun saveEstudiante(onSuccess: () -> Unit) {
        // Validar campos
        var hasErrors = false

        if (_uiState.value.nombre.isBlank()) {
            _uiState.value = _uiState.value.copy(nombreError = "El nombre es requerido")
            hasErrors = true
        }

        if (_uiState.value.apellido.isBlank()) {
            _uiState.value = _uiState.value.copy(apellidoError = "El apellido es requerido")
            hasErrors = true
        }

        if (_uiState.value.codigo.isBlank()) {
            _uiState.value = _uiState.value.copy(codigoError = "El código es requerido")
            hasErrors = true
        }

        // Validar email si se proporcionó
        if (_uiState.value.email.isNotBlank()) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches()) {
                _uiState.value = _uiState.value.copy(emailError = "Email inválido")
                hasErrors = true
            }
        }

        if (hasErrors) return

        // Guardar estudiante
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)

                val estudiante = if (_uiState.value.isEditMode && studentId != -1L) {
                    // Actualizar estudiante existente
                    val existing = estudianteRepository.getEstudianteByIdSync(studentId)
                    existing?.copy(
                        nombre = _uiState.value.nombre,
                        apellido = _uiState.value.apellido,
                        codigo = _uiState.value.codigo,
                        email = _uiState.value.email.ifBlank { null }
                    )
                } else {
                    // Crear nuevo estudiante
                    Estudiante(
                        nombre = _uiState.value.nombre,
                        apellido = _uiState.value.apellido,
                        codigo = _uiState.value.codigo,
                        email = _uiState.value.email.ifBlank { null },
                        grupoId = groupId,
                        activo = true
                    )
                }

                if (estudiante != null) {
                    if (_uiState.value.isEditMode) {
                        estudianteRepository.updateEstudiante(estudiante)
                    } else {
                        estudianteRepository.insertEstudiante(estudiante)
                    }
                    onSuccess()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = if (e.message?.contains("UNIQUE constraint") == true) {
                        "Ya existe un estudiante con ese código"
                    } else {
                        e.message
                    },
                    isSaving = false
                )
            }
        }
    }
}

/**
 * Estado UI del formulario de estudiante
 */
data class AddEditStudentUiState(
    val groupId: Long = -1L,
    val nombre: String = "",
    val nombreError: String? = null,
    val apellido: String = "",
    val apellidoError: String? = null,
    val codigo: String = "",
    val codigoError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)
