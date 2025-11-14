package sv.edu.catolica.asistedocente.ui.screens.groups

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sv.edu.catolica.asistedocente.data.local.entities.Estudiante
import sv.edu.catolica.asistedocente.data.local.entities.Grupo
import sv.edu.catolica.asistedocente.data.repository.EstudianteRepository
import sv.edu.catolica.asistedocente.data.repository.GrupoRepository
import javax.inject.Inject

/**
 * ViewModel para la pantalla de detalle de grupo
 * Muestra información del grupo y lista de estudiantes
 */
@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    private val grupoRepository: GrupoRepository,
    private val estudianteRepository: EstudianteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupId: Long = savedStateHandle.get<Long>("groupId") ?: -1L

    private val _uiState = MutableStateFlow<GroupDetailUiState>(GroupDetailUiState.Loading)
    val uiState: StateFlow<GroupDetailUiState> = _uiState.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private var currentGrupo: Grupo? = null

    init {
        loadGroupDetail()
    }

    /**
     * Carga los detalles del grupo y sus estudiantes
     */
    private fun loadGroupDetail() {
        viewModelScope.launch {
            try {
                // Obtener grupo
                val grupo = grupoRepository.getGrupoByIdSync(groupId)
                if (grupo == null) {
                    _uiState.value = GroupDetailUiState.Error("Grupo no encontrado")
                    return@launch
                }
                currentGrupo = grupo

                // Observar estudiantes del grupo
                estudianteRepository.getEstudiantesByGrupo(groupId).collect { estudiantes ->
                    _uiState.value = GroupDetailUiState.Success(
                        grupo = grupo,
                        estudiantes = estudiantes
                    )
                }
            } catch (e: Exception) {
                _uiState.value = GroupDetailUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Recarga los datos
     */
    fun refresh() {
        _uiState.value = GroupDetailUiState.Loading
        loadGroupDetail()
    }

    /**
     * Muestra el diálogo de confirmación de eliminación
     */
    fun showDeleteConfirmation() {
        _showDeleteDialog.value = true
    }

    /**
     * Oculta el diálogo de confirmación
     */
    fun dismissDeleteDialog() {
        _showDeleteDialog.value = false
    }

    /**
     * Elimina un estudiante del grupo
     */
    fun deleteEstudiante(estudianteId: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                estudianteRepository.desactivarEstudiante(estudianteId)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error al eliminar estudiante")
            }
        }
    }

    /**
     * Elimina el grupo (marca como inactivo)
     */
    fun deleteGroup(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                grupoRepository.deleteGrupo(groupId)
                _showDeleteDialog.value = false
                onSuccess()
            } catch (e: Exception) {
                _showDeleteDialog.value = false
                onError(e.message ?: "Error al eliminar grupo")
            }
        }
    }

    /**
     * Obtiene estadísticas básicas del grupo
     */
    fun getGroupStats(): GroupStats {
        val state = _uiState.value
        return if (state is GroupDetailUiState.Success) {
            GroupStats(
                totalStudents = state.estudiantes.size,
                activeStudents = state.estudiantes.count { it.activo }
            )
        } else {
            GroupStats(0, 0)
        }
    }
}

/**
 * Estados de la pantalla de detalle de grupo
 */
sealed class GroupDetailUiState {
    object Loading : GroupDetailUiState()
    data class Success(
        val grupo: Grupo,
        val estudiantes: List<Estudiante>
    ) : GroupDetailUiState()
    data class Error(val message: String) : GroupDetailUiState()
}

/**
 * Estadísticas del grupo
 */
data class GroupStats(
    val totalStudents: Int,
    val activeStudents: Int
)
