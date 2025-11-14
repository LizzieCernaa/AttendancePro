package sv.edu.catolica.asistedocente.ui.screens.attendance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sv.edu.catolica.asistedocente.data.local.entities.Estudiante
import sv.edu.catolica.asistedocente.data.local.entities.EstadoAsistencia
import sv.edu.catolica.asistedocente.data.local.entities.RegistroAsistencia
import sv.edu.catolica.asistedocente.data.repository.AsistenciaRepository
import sv.edu.catolica.asistedocente.data.repository.EstudianteRepository
import sv.edu.catolica.asistedocente.data.repository.GrupoRepository
import sv.edu.catolica.asistedocente.utils.DateUtils
import javax.inject.Inject

/**
 * ViewModel para la pantalla de toma de asistencia
 * Esta es la pantalla MÁS CRÍTICA de la aplicación
 * Debe ser extremadamente rápida y eficiente
 */
@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val estudianteRepository: EstudianteRepository,
    private val asistenciaRepository: AsistenciaRepository,
    private val grupoRepository: GrupoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val groupId: Long = savedStateHandle.get<Long>("groupId") ?: -1L
    private val dateParam: Long = savedStateHandle.get<Long>("date") ?: -1L

    private val _uiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Loading)
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    // Fecha seleccionada para tomar asistencia
    private val _selectedDate = MutableStateFlow(
        if (dateParam != -1L) dateParam else DateUtils.getTodayStart()
    )
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    // Mapa de estados de asistencia por estudiante
    private val _attendanceStates = MutableStateFlow<Map<Long, EstadoAsistencia>>(emptyMap())
    val attendanceStates: StateFlow<Map<Long, EstadoAsistencia>> = _attendanceStates.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        loadAttendanceData()
    }

    /**
     * Carga los datos de asistencia para el grupo y fecha seleccionada
     */
    private fun loadAttendanceData() {
        viewModelScope.launch {
            try {
                // Obtener grupo
                val grupo = grupoRepository.getGrupoByIdSync(groupId)
                if (grupo == null) {
                    _uiState.value = AttendanceUiState.Error("Grupo no encontrado")
                    return@launch
                }

                // Obtener estudiantes del grupo
                estudianteRepository.getEstudiantesByGrupo(groupId).collect { estudiantes ->
                    if (estudiantes.isEmpty()) {
                        _uiState.value = AttendanceUiState.Empty(grupo.nombre)
                        return@collect
                    }

                    // Cargar registros de asistencia existentes para esta fecha
                    val registros = asistenciaRepository.getRegistrosByGrupoAndFechaSync(
                        groupId,
                        _selectedDate.value
                    )

                    // Crear mapa de estados
                    val estadosMap = mutableMapOf<Long, EstadoAsistencia>()
                    registros.forEach { registro ->
                        estadosMap[registro.estudianteId] = registro.estado
                    }
                    _attendanceStates.value = estadosMap

                    // Actualizar UI con los estudiantes
                    _uiState.value = AttendanceUiState.Success(
                        groupName = grupo.nombre,
                        students = estudiantes
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AttendanceUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Actualiza el estado de asistencia de un estudiante
     */
    fun updateAttendanceState(studentId: Long, estado: EstadoAsistencia) {
        val currentStates = _attendanceStates.value.toMutableMap()
        currentStates[studentId] = estado
        _attendanceStates.value = currentStates
    }

    /**
     * Marca todos los estudiantes como presentes
     */
    fun markAllAsPresent() {
        val state = _uiState.value
        if (state is AttendanceUiState.Success) {
            val allPresent = mutableMapOf<Long, EstadoAsistencia>()
            state.students.forEach { estudiante ->
                allPresent[estudiante.id] = EstadoAsistencia.PRESENTE
            }
            _attendanceStates.value = allPresent
        }
    }

    /**
     * Limpia todos los estados
     */
    fun clearAll() {
        _attendanceStates.value = emptyMap()
    }

    /**
     * Cambia la fecha seleccionada
     */
    fun changeDate(newDate: Long) {
        _selectedDate.value = newDate
        loadAttendanceData()
    }

    /**
     * Guarda todos los registros de asistencia
     */
    fun saveAttendance(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _isSaving.value = true

                val state = _uiState.value
                if (state !is AttendanceUiState.Success) {
                    onError("Estado inválido")
                    return@launch
                }

                // Crear registros para cada estudiante
                val registros = state.students.mapNotNull { estudiante ->
                    val estado = _attendanceStates.value[estudiante.id]
                    if (estado != null) {
                        RegistroAsistencia(
                            estudianteId = estudiante.id,
                            grupoId = groupId,
                            fecha = _selectedDate.value,
                            estado = estado,
                            horaRegistro = System.currentTimeMillis()
                        )
                    } else {
                        null
                    }
                }

                // Guardar o actualizar registros
                registros.forEach { registro ->
                    // Verificar si ya existe un registro para este estudiante y fecha
                    val existing = asistenciaRepository.getRegistroByEstudianteAndFecha(
                        registro.estudianteId,
                        registro.fecha
                    )

                    if (existing != null) {
                        // Actualizar registro existente
                        asistenciaRepository.updateRegistro(
                            existing.copy(
                                estado = registro.estado,
                                horaRegistro = System.currentTimeMillis()
                            )
                        )
                    } else {
                        // Insertar nuevo registro
                        asistenciaRepository.insertRegistro(registro)
                    }
                }

                _isSaving.value = false
                onSuccess()
            } catch (e: Exception) {
                _isSaving.value = false
                onError(e.message ?: "Error al guardar")
            }
        }
    }

    /**
     * Obtiene el conteo de estudiantes por estado
     */
    fun getAttendanceCount(): AttendanceCount {
        val states = _attendanceStates.value
        return AttendanceCount(
            presentes = states.count { it.value == EstadoAsistencia.PRESENTE },
            ausentes = states.count { it.value == EstadoAsistencia.AUSENTE },
            tardanzas = states.count { it.value == EstadoAsistencia.TARDANZA },
            justificados = states.count { it.value == EstadoAsistencia.JUSTIFICADO },
            sinMarcar = (_uiState.value as? AttendanceUiState.Success)?.students?.size?.minus(states.size) ?: 0
        )
    }
}

/**
 * Estados de la pantalla de asistencia
 */
sealed class AttendanceUiState {
    object Loading : AttendanceUiState()
    data class Empty(val groupName: String) : AttendanceUiState()
    data class Success(
        val groupName: String,
        val students: List<Estudiante>
    ) : AttendanceUiState()
    data class Error(val message: String) : AttendanceUiState()
}

/**
 * Conteo de asistencia
 */
data class AttendanceCount(
    val presentes: Int = 0,
    val ausentes: Int = 0,
    val tardanzas: Int = 0,
    val justificados: Int = 0,
    val sinMarcar: Int = 0
)
