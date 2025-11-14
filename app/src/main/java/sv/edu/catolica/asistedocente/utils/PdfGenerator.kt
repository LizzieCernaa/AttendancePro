package sv.edu.catolica.asistedocente.utils

import android.content.Context
import android.os.Environment
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import sv.edu.catolica.asistedocente.data.local.entities.EstadoAsistencia
import sv.edu.catolica.asistedocente.data.local.entities.Estudiante
import sv.edu.catolica.asistedocente.data.local.entities.Grupo
import sv.edu.catolica.asistedocente.data.local.entities.RegistroAsistencia
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Generador de reportes PDF profesionales
 * Usa iText7 para crear documentos PDF con tablas y estadísticas
 */
class PdfGenerator(private val context: Context) {

    companion object {
        private const val REPORTS_DIR = "reportes"

        // Colores corporativos
        private val COLOR_PRIMARY = DeviceRgb(25, 118, 210) // Azul
        private val COLOR_SUCCESS = DeviceRgb(76, 175, 80) // Verde
        private val COLOR_ERROR = DeviceRgb(244, 67, 54) // Rojo
        private val COLOR_WARNING = DeviceRgb(255, 152, 0) // Naranja
        private val COLOR_INFO = DeviceRgb(33, 150, 243) // Azul claro
    }

    /**
     * Genera reporte de asistencia de un estudiante individual
     */
    fun generateStudentAttendanceReport(
        estudiante: Estudiante,
        registros: List<RegistroAsistencia>,
        grupo: Grupo,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): File {
        val fileName = "reporte_${estudiante.codigo}_${System.currentTimeMillis()}.pdf"
        val file = File(getReportsDirectory(), fileName)

        val writer = PdfWriter(file)
        val pdf = PdfDocument(writer)
        val document = Document(pdf, PageSize.A4)

        // Configurar márgenes
        document.setMargins(40f, 40f, 40f, 40f)

        try {
            // Título del reporte
            addTitle(document, "REPORTE DE ASISTENCIA")

            // Información del estudiante
            addStudentInfo(document, estudiante, grupo, fechaInicio, fechaFin)

            // Espaciado
            document.add(Paragraph("\n"))

            // Tabla de registros de asistencia
            addAttendanceTable(document, registros)

            // Espaciado
            document.add(Paragraph("\n"))

            // Estadísticas
            addStatisticsSection(document, registros)

            // Pie de página
            addFooter(document)

        } finally {
            document.close()
        }

        return file
    }

    /**
     * Genera reporte de asistencia para un grupo completo
     */
    fun generateGroupAttendanceReport(
        grupo: Grupo,
        estudiantes: List<Estudiante>,
        registrosPorEstudiante: Map<Long, List<RegistroAsistencia>>,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): File {
        val fileName = "reporte_grupo_${grupo.id}_${System.currentTimeMillis()}.pdf"
        val file = File(getReportsDirectory(), fileName)

        val writer = PdfWriter(file)
        val pdf = PdfDocument(writer)
        val document = Document(pdf, PageSize.A4.rotate()) // Horizontal para más espacio

        document.setMargins(40f, 40f, 40f, 40f)

        try {
            // Título
            addTitle(document, "REPORTE DE ASISTENCIA DEL GRUPO")

            // Información del grupo
            addGroupInfo(document, grupo, fechaInicio, fechaFin)

            document.add(Paragraph("\n"))

            // Tabla consolidada de asistencia por estudiante
            addGroupAttendanceTable(document, estudiantes, registrosPorEstudiante)

            document.add(Paragraph("\n"))

            // Estadísticas generales del grupo
            addGroupStatistics(document, registrosPorEstudiante)

            // Pie de página
            addFooter(document)

        } finally {
            document.close()
        }

        return file
    }

    /**
     * Agrega el título principal del documento
     */
    private fun addTitle(document: Document, titleText: String) {
        val title = Paragraph(titleText)
            .setFontSize(20f)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20f)
            .setFontColor(COLOR_PRIMARY)

        document.add(title)
    }

    /**
     * Agrega información del estudiante
     */
    private fun addStudentInfo(
        document: Document,
        estudiante: Estudiante,
        grupo: Grupo,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ) {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 2f)))
            .setWidth(UnitValue.createPercentValue(100f))

        addInfoRow(table, "Estudiante:", "${estudiante.nombre} ${estudiante.apellido}")
        addInfoRow(table, "Código:", estudiante.codigo)
        addInfoRow(table, "Grupo:", grupo.nombre)
        addInfoRow(table, "Materia:", grupo.materia)
        addInfoRow(table, "Período:", "${formatDate(fechaInicio)} - ${formatDate(fechaFin)}")

        document.add(table)
    }

    /**
     * Agrega información del grupo
     */
    private fun addGroupInfo(
        document: Document,
        grupo: Grupo,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ) {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 2f)))
            .setWidth(UnitValue.createPercentValue(60f))

        addInfoRow(table, "Grupo:", grupo.nombre)
        addInfoRow(table, "Materia:", grupo.materia)
        addInfoRow(table, "Horario:", grupo.horario ?: "N/A")
        addInfoRow(table, "Período:", "${formatDate(fechaInicio)} - ${formatDate(fechaFin)}")

        document.add(table)
    }

    /**
     * Agrega una fila de información (label: valor)
     */
    private fun addInfoRow(table: Table, label: String, value: String) {
        val labelCell = Cell()
            .add(Paragraph(label).setBold())
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setBorder(SolidBorder(ColorConstants.GRAY, 1f))

        val valueCell = Cell()
            .add(Paragraph(value))
            .setBorder(SolidBorder(ColorConstants.GRAY, 1f))

        table.addCell(labelCell)
        table.addCell(valueCell)
    }

    /**
     * Agrega tabla de registros de asistencia
     */
    private fun addAttendanceTable(document: Document, registros: List<RegistroAsistencia>) {
        val table = Table(floatArrayOf(1f, 3f, 2f, 3f))
            .setWidth(UnitValue.createPercentValue(100f))

        // Encabezados
        table.addHeaderCell(createHeaderCell("N°"))
        table.addHeaderCell(createHeaderCell("Fecha"))
        table.addHeaderCell(createHeaderCell("Estado"))
        table.addHeaderCell(createHeaderCell("Notas"))

        // Datos
        registros.forEachIndexed { index, registro ->
            table.addCell((index + 1).toString())
            table.addCell(formatDate(registro.fecha))

            val estadoCell = Cell()
                .add(Paragraph(getEstadoText(registro.estado)))
                .setBackgroundColor(getEstadoColor(registro.estado))
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
            table.addCell(estadoCell)

            table.addCell(registro.notas ?: "-")
        }

        document.add(table)
    }

    /**
     * Agrega tabla consolidada de asistencia por estudiante
     */
    private fun addGroupAttendanceTable(
        document: Document,
        estudiantes: List<Estudiante>,
        registrosPorEstudiante: Map<Long, List<RegistroAsistencia>>
    ) {
        val table = Table(floatArrayOf(1f, 3f, 2f, 2f, 2f, 2f, 2f, 2f))
            .setWidth(UnitValue.createPercentValue(100f))

        // Encabezados
        table.addHeaderCell(createHeaderCell("N°"))
        table.addHeaderCell(createHeaderCell("Estudiante"))
        table.addHeaderCell(createHeaderCell("Código"))
        table.addHeaderCell(createHeaderCell("Total"))
        table.addHeaderCell(createHeaderCell("Presentes"))
        table.addHeaderCell(createHeaderCell("Ausentes"))
        table.addHeaderCell(createHeaderCell("Tardanzas"))
        table.addHeaderCell(createHeaderCell("%"))

        // Datos
        estudiantes.forEachIndexed { index, estudiante ->
            val registros = registrosPorEstudiante[estudiante.id] ?: emptyList()
            val stats = calculateStatistics(registros)

            table.addCell((index + 1).toString())
            table.addCell("${estudiante.nombre} ${estudiante.apellido}")
            table.addCell(estudiante.codigo)
            table.addCell(stats.total.toString())
            table.addCell(stats.presentes.toString())
            table.addCell(stats.ausentes.toString())
            table.addCell(stats.tardanzas.toString())
            table.addCell(String.format("%.1f%%", stats.porcentaje))
        }

        document.add(table)
    }

    /**
     * Agrega sección de estadísticas
     */
    private fun addStatisticsSection(document: Document, registros: List<RegistroAsistencia>) {
        val title = Paragraph("ESTADÍSTICAS")
            .setBold()
            .setFontSize(16f)
            .setMarginTop(20f)
            .setFontColor(COLOR_PRIMARY)

        document.add(title)

        val stats = calculateStatistics(registros)

        val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
            .setWidth(UnitValue.createPercentValue(50f))

        addStatsRow(table, "Total de clases:", stats.total.toString())
        addStatsRow(table, "Presentes:", stats.presentes.toString())
        addStatsRow(table, "Ausentes:", stats.ausentes.toString())
        addStatsRow(table, "Tardanzas:", stats.tardanzas.toString())
        addStatsRow(table, "Justificados:", stats.justificados.toString())
        addStatsRow(table, "Porcentaje de asistencia:", String.format("%.2f%%", stats.porcentaje))

        document.add(table)
    }

    /**
     * Agrega estadísticas generales del grupo
     */
    private fun addGroupStatistics(
        document: Document,
        registrosPorEstudiante: Map<Long, List<RegistroAsistencia>>
    ) {
        val title = Paragraph("ESTADÍSTICAS GENERALES")
            .setBold()
            .setFontSize(16f)
            .setMarginTop(20f)
            .setFontColor(COLOR_PRIMARY)

        document.add(title)

        // Calcular estadísticas consolidadas
        var totalPresentes = 0
        var totalAusentes = 0
        var totalTardanzas = 0
        var totalJustificados = 0
        var totalRegistros = 0

        registrosPorEstudiante.values.forEach { registros ->
            registros.forEach { registro ->
                totalRegistros++
                when (registro.estado) {
                    EstadoAsistencia.PRESENTE -> totalPresentes++
                    EstadoAsistencia.AUSENTE -> totalAusentes++
                    EstadoAsistencia.TARDANZA -> totalTardanzas++
                    EstadoAsistencia.JUSTIFICADO -> totalJustificados++
                }
            }
        }

        val porcentajeGeneral = if (totalRegistros > 0) {
            (totalPresentes * 100f) / totalRegistros
        } else 0f

        val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
            .setWidth(UnitValue.createPercentValue(50f))

        addStatsRow(table, "Total de estudiantes:", registrosPorEstudiante.size.toString())
        addStatsRow(table, "Total de registros:", totalRegistros.toString())
        addStatsRow(table, "Total presentes:", totalPresentes.toString())
        addStatsRow(table, "Total ausentes:", totalAusentes.toString())
        addStatsRow(table, "Total tardanzas:", totalTardanzas.toString())
        addStatsRow(table, "Total justificados:", totalJustificados.toString())
        addStatsRow(table, "Porcentaje promedio:", String.format("%.2f%%", porcentajeGeneral))

        document.add(table)
    }

    /**
     * Agrega fila de estadística
     */
    private fun addStatsRow(table: Table, label: String, value: String) {
        table.addCell(Cell().add(Paragraph(label).setBold()))
        table.addCell(Cell().add(Paragraph(value)))
    }

    /**
     * Crea celda de encabezado
     */
    private fun createHeaderCell(text: String): Cell {
        return Cell()
            .add(Paragraph(text).setBold().setFontColor(ColorConstants.WHITE))
            .setBackgroundColor(COLOR_PRIMARY)
            .setTextAlignment(TextAlignment.CENTER)
    }

    /**
     * Obtiene el color según el estado de asistencia
     */
    private fun getEstadoColor(estado: EstadoAsistencia): DeviceRgb {
        return when (estado) {
            EstadoAsistencia.PRESENTE -> COLOR_SUCCESS
            EstadoAsistencia.AUSENTE -> COLOR_ERROR
            EstadoAsistencia.TARDANZA -> COLOR_WARNING
            EstadoAsistencia.JUSTIFICADO -> COLOR_INFO
        }
    }

    /**
     * Obtiene el texto del estado de asistencia
     */
    private fun getEstadoText(estado: EstadoAsistencia): String {
        return when (estado) {
            EstadoAsistencia.PRESENTE -> "Presente"
            EstadoAsistencia.AUSENTE -> "Ausente"
            EstadoAsistencia.TARDANZA -> "Tardanza"
            EstadoAsistencia.JUSTIFICADO -> "Justificado"
        }
    }

    /**
     * Agrega pie de página con fecha de generación
     */
    private fun addFooter(document: Document) {
        val footer = Paragraph("Generado el ${DateUtils.formatDate(System.currentTimeMillis(), "dd/MM/yyyy HH:mm")}")
            .setFontSize(8f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(30f)

        document.add(footer)
    }

    /**
     * Formatea una fecha
     */
    private fun formatDate(timestamp: Long): String {
        return DateUtils.formatDate(timestamp, "dd/MM/yyyy")
    }

    private fun formatDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    /**
     * Calcula estadísticas de una lista de registros
     */
    private fun calculateStatistics(registros: List<RegistroAsistencia>): AttendanceStats {
        val total = registros.size
        val presentes = registros.count { it.estado == EstadoAsistencia.PRESENTE }
        val ausentes = registros.count { it.estado == EstadoAsistencia.AUSENTE }
        val tardanzas = registros.count { it.estado == EstadoAsistencia.TARDANZA }
        val justificados = registros.count { it.estado == EstadoAsistencia.JUSTIFICADO }
        val porcentaje = if (total > 0) (presentes * 100f) / total else 0f

        return AttendanceStats(total, presentes, ausentes, tardanzas, justificados, porcentaje)
    }

    /**
     * Obtiene el directorio de reportes
     */
    private fun getReportsDirectory(): File {
        val reportsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), REPORTS_DIR)
        if (!reportsDir.exists()) {
            reportsDir.mkdirs()
        }
        return reportsDir
    }

    /**
     * Clase de datos para estadísticas de asistencia
     */
    private data class AttendanceStats(
        val total: Int,
        val presentes: Int,
        val ausentes: Int,
        val tardanzas: Int,
        val justificados: Int,
        val porcentaje: Float
    )
}
