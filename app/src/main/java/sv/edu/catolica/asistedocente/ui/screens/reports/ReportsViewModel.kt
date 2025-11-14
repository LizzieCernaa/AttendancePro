package sv.edu.catolica.asistedocente.ui.screens.reports

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import sv.edu.catolica.asistedocente.data.local.entities.EstadoAsistencia
import sv.edu.catolica.asistedocente.data.local.entities.Grupo
import sv.edu.catolica.asistedocente.data.repository.AsistenciaRepository
import sv.edu.catolica.asistedocente.data.repository.EstudianteRepository
import sv.edu.catolica.asistedocente.data.repository.GrupoRepository
import sv.edu.catolica.asistedocente.utils.DateUtils
import sv.edu.catolica.asistedocente.utils.PdfGenerator
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/**
 * ViewModel para la pantalla de reportes
 * Genera estadísticas de asistencia por grupo y rango de fechas
 */
@HiltViewModel
class ReportsViewModel @Inject constructor(
    application: Application,
    private val grupoRepository: GrupoRepository,
    private val estudianteRepository: EstudianteRepository,
    private val asistenciaRepository: AsistenciaRepository
) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext
    private val pdfGenerator = PdfGenerator(context)

    private val _grupos = MutableStateFlow<List<Grupo>>(emptyList())
    val grupos: StateFlow<List<Grupo>> = _grupos.asStateFlow()

    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    val selectedGroupId: StateFlow<Long?> = _selectedGroupId.asStateFlow()

    private val _startDate = MutableStateFlow(DateUtils.getTodayStart())
    val startDate: StateFlow<Long> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow(DateUtils.getTodayStart())
    val endDate: StateFlow<Long> = _endDate.asStateFlow()

    private val _reportData = MutableStateFlow<ReportData?>(null)
    val reportData: StateFlow<ReportData?> = _reportData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadGrupos()
    }

    /**
     * Carga todos los grupos activos
     */
    private fun loadGrupos() {
        viewModelScope.launch {
            grupoRepository.getAllGruposActivos().collect { grupos ->
                _grupos.value = grupos
                // Seleccionar el primer grupo por defecto
                if (_selectedGroupId.value == null && grupos.isNotEmpty()) {
                    _selectedGroupId.value = grupos.first().id
                    generateReport()
                }
            }
        }
    }

    /**
     * Selecciona un grupo
     */
    fun selectGroup(groupId: Long) {
        _selectedGroupId.value = groupId
        generateReport()
    }

    /**
     * Establece la fecha de inicio
     */
    fun setStartDate(date: Long) {
        _startDate.value = date
        if (date > _endDate.value) {
            _endDate.value = date
        }
        generateReport()
    }

    /**
     * Establece la fecha de fin
     */
    fun setEndDate(date: Long) {
        _endDate.value = date
        if (date < _startDate.value) {
            _startDate.value = date
        }
        generateReport()
    }

    /**
     * Genera el reporte con los filtros actuales
     */
    fun generateReport() {
        val groupId = _selectedGroupId.value ?: return

        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Obtener estudiantes del grupo
                val estudiantes = estudianteRepository.getEstudiantesByGrupoSync(groupId)
                val totalEstudiantes = estudiantes.size

                // Obtener registros de asistencia en el rango de fechas
                val registros = asistenciaRepository.getRegistrosByGrupoAndDateRange(
                    groupId,
                    _startDate.value,
                    _endDate.value
                )

                // Calcular estadísticas
                val presentes = registros.count { it.estado == EstadoAsistencia.PRESENTE }
                val ausentes = registros.count { it.estado == EstadoAsistencia.AUSENTE }
                val tardanzas = registros.count { it.estado == EstadoAsistencia.TARDANZA }
                val justificados = registros.count { it.estado == EstadoAsistencia.JUSTIFICADO }
                val totalRegistros = registros.size

                // Calcular porcentajes
                val porcentajePresentes = if (totalRegistros > 0) (presentes * 100f / totalRegistros) else 0f
                val porcentajeAusentes = if (totalRegistros > 0) (ausentes * 100f / totalRegistros) else 0f
                val porcentajeTardanzas = if (totalRegistros > 0) (tardanzas * 100f / totalRegistros) else 0f
                val porcentajeJustificados = if (totalRegistros > 0) (justificados * 100f / totalRegistros) else 0f

                // Calcular días únicos
                val diasRegistrados = registros.map {
                    DateUtils.getStartOfDay(it.fecha)
                }.distinct().size

                // Calcular asistencia promedio por estudiante
                val asistenciaPromedioPorEstudiante = if (totalEstudiantes > 0 && diasRegistrados > 0) {
                    (presentes.toFloat() / (totalEstudiantes * diasRegistrados)) * 100f
                } else {
                    0f
                }

                _reportData.value = ReportData(
                    totalEstudiantes = totalEstudiantes,
                    diasRegistrados = diasRegistrados,
                    totalRegistros = totalRegistros,
                    presentes = presentes,
                    ausentes = ausentes,
                    tardanzas = tardanzas,
                    justificados = justificados,
                    porcentajePresentes = porcentajePresentes,
                    porcentajeAusentes = porcentajeAusentes,
                    porcentajeTardanzas = porcentajeTardanzas,
                    porcentajeJustificados = porcentajeJustificados,
                    porcentajeAsistenciaGeneral = asistenciaPromedioPorEstudiante
                )

                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _reportData.value = null
            }
        }
    }

    /**
     * Exporta el reporte a PDF
     */
    fun exportToPdf(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val groupId = _selectedGroupId.value
        if (groupId == null) {
            onError("Seleccione un grupo para generar el reporte")
            return
        }

        viewModelScope.launch {
            try {
                // Obtener datos del grupo
                val grupo = grupoRepository.getGrupoById(groupId).firstOrNull()
                if (grupo == null) {
                    onError("No se encontró el grupo")
                    return@launch
                }

                // Obtener estudiantes del grupo
                val estudiantes = estudianteRepository.getEstudiantesByGrupoSync(groupId)
                if (estudiantes.isEmpty()) {
                    onError("El grupo no tiene estudiantes")
                    return@launch
                }

                // Obtener registros de asistencia en el rango de fechas
                val registros = asistenciaRepository.getRegistrosByGrupoAndDateRange(
                    groupId,
                    _startDate.value,
                    _endDate.value
                )

                // Agrupar registros por estudiante
                val registrosPorEstudiante = registros.groupBy { it.estudianteId }

                // Convertir timestamps a LocalDate
                val fechaInicio = Instant.ofEpochMilli(_startDate.value)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val fechaFin = Instant.ofEpochMilli(_endDate.value)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                // Generar PDF
                val pdfFile = pdfGenerator.generateGroupAttendanceReport(
                    grupo = grupo,
                    estudiantes = estudiantes,
                    registrosPorEstudiante = registrosPorEstudiante,
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin
                )

                onSuccess(pdfFile.absolutePath)
            } catch (e: Exception) {
                onError("Error al generar PDF: ${e.message}")
            }
        }
    }

    /**
     * Abre el PDF generado con una aplicación externa
     */
    fun openPdf(filePath: String) {
        try {
            val file = File(filePath)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Comparte el PDF generado
     */
    fun sharePdf(filePath: String) {
        try {
            val file = File(filePath)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            val chooser = Intent.createChooser(intent, "Compartir reporte PDF")
            chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Exporta el reporte a Excel
     */
    fun exportToExcel(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        // TODO: Implementar generación de Excel con Apache POI
        onError("La exportación a Excel aún no está implementada")
    }
}

/**
 * Datos del reporte de asistencia
 */
data class ReportData(
    val totalEstudiantes: Int,
    val diasRegistrados: Int,
    val totalRegistros: Int,
    val presentes: Int,
    val ausentes: Int,
    val tardanzas: Int,
    val justificados: Int,
    val porcentajePresentes: Float,
    val porcentajeAusentes: Float,
    val porcentajeTardanzas: Float,
    val porcentajeJustificados: Float,
    val porcentajeAsistenciaGeneral: Float
)
