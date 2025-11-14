package sv.edu.catolica.asistedocente.ui.screens.groups

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sv.edu.catolica.asistedocente.data.local.entities.Grupo
import sv.edu.catolica.asistedocente.data.repository.GrupoRepository
import sv.edu.catolica.asistedocente.utils.AuthHelper
import javax.inject.Inject

/**
 * ViewModel para crear y editar grupos
 */
@HiltViewModel
class AddEditGroupViewModel @Inject constructor(
    application: Application,
    private val grupoRepository: GrupoRepository,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val groupId: Long = savedStateHandle.get<Long>("groupId") ?: -1L
    private val docenteId: Long = AuthHelper.getDocenteId(application.applicationContext) ?: -1L

    private val _uiState = MutableStateFlow(AddEditGroupUiState())
    val uiState: StateFlow<AddEditGroupUiState> = _uiState.asStateFlow()

    init {
        if (groupId != -1L) {
            loadGrupo(groupId)
        }
    }

    /**
     * Carga los datos del grupo para edición
     */
    private fun loadGrupo(id: Long) {
        viewModelScope.launch {
            try {
                val grupo = grupoRepository.getGrupoByIdSync(id)
                if (grupo != null) {
                    _uiState.value = _uiState.value.copy(
                        nombre = grupo.nombre,
                        materia = grupo.materia,
                        horario = grupo.horario ?: "",
                        descripcion = grupo.descripcion ?: "",
                        isEditMode = true,
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
     * Actualiza el nombre del grupo
     */
    fun onNombreChange(nombre: String) {
        _uiState.value = _uiState.value.copy(
            nombre = nombre,
            nombreError = null
        )
    }

    /**
     * Actualiza la materia
     */
    fun onMateriaChange(materia: String) {
        _uiState.value = _uiState.value.copy(
            materia = materia,
            materiaError = null
        )
    }

    /**
     * Actualiza el horario
     */
    fun onHorarioChange(horario: String) {
        _uiState.value = _uiState.value.copy(horario = horario)
    }

    /**
     * Actualiza la descripción
     */
    fun onDescripcionChange(descripcion: String) {
        _uiState.value = _uiState.value.copy(descripcion = descripcion)
    }

    /**
     * Valida y guarda el grupo
     */
    fun saveGrupo(onSuccess: () -> Unit) {
        // Validar campos
        var hasErrors = false

        if (_uiState.value.nombre.isBlank()) {
            _uiState.value = _uiState.value.copy(nombreError = "El nombre es requerido")
            hasErrors = true
        }

        if (_uiState.value.materia.isBlank()) {
            _uiState.value = _uiState.value.copy(materiaError = "La materia es requerida")
            hasErrors = true
        }

        if (hasErrors) return

        // Guardar grupo
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)

                val grupo = if (_uiState.value.isEditMode && groupId != -1L) {
                    // Actualizar grupo existente
                    val existing = grupoRepository.getGrupoByIdSync(groupId)
                    existing?.copy(
                        nombre = _uiState.value.nombre,
                        materia = _uiState.value.materia,
                        horario = _uiState.value.horario.ifBlank { null },
                        descripcion = _uiState.value.descripcion.ifBlank { null }
                    )
                } else {
                    // Crear nuevo grupo asociado al docente logueado
                    Grupo(
                        nombre = _uiState.value.nombre,
                        materia = _uiState.value.materia,
                        horario = _uiState.value.horario.ifBlank { null },
                        descripcion = _uiState.value.descripcion.ifBlank { null },
                        docenteId = docenteId,
                        activo = true
                    )
                }

                if (grupo != null) {
                    if (_uiState.value.isEditMode) {
                        grupoRepository.updateGrupo(grupo)
                    } else {
                        grupoRepository.insertGrupo(grupo)
                    }
                    onSuccess()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isSaving = false
                )
            }
        }
    }
}

/**
 * Estado UI del formulario de grupo
 */
data class AddEditGroupUiState(
    val nombre: String = "",
    val nombreError: String? = null,
    val materia: String = "",
    val materiaError: String? = null,
    val horario: String = "",
    val descripcion: String = "",
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)
