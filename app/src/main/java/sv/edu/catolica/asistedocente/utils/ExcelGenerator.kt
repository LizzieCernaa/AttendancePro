package sv.edu.catolica.asistedocente.utils

import android.content.Context
import android.os.Environment
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import sv.edu.catolica.asistedocente.data.local.entities.EstadoAsistencia
import sv.edu.catolica.asistedocente.data.local.entities.Estudiante
import sv.edu.catolica.asistedocente.data.local.entities.Grupo
import sv.edu.catolica.asistedocente.data.local.entities.RegistroAsistencia
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Generador de reportes Excel (.xlsx)
 * Usa Apache POI para crear archivos Excel con formato profesional
 */
class ExcelGenerator(private val context: Context) {

    companion object {
        private const val REPORTS_DIR = "reportes"
    }

    /**
     * Genera reporte Excel de asistencia de un estudiante
     */
    fun generateStudentAttendanceReport(
        estudiante: Estudiante,
        registros: List<RegistroAsistencia>,
        grupo: Grupo,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): File {
        val workbook = XSSFWorkbook()

        try {
            // Crear hoja
            val sheet = workbook.createSheet("Asistencia")

            var rowNum = 0

            // Título
            val titleRow = sheet.createRow(rowNum++)
            val titleCell = titleRow.createCell(0)
            titleCell.setCellValue("REPORTE DE ASISTENCIA")
            titleCell.cellStyle = createTitleStyle(workbook)
            sheet.addMergedRegion(org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3))

            rowNum++ // Fila vacía

            // Información del estudiante
            addInfoRow(sheet, workbook, rowNum++, "Estudiante:", "${estudiante.nombre} ${estudiante.apellido}")
            addInfoRow(sheet, workbook, rowNum++, "Código:", estudiante.codigo)
            addInfoRow(sheet, workbook, rowNum++, "Grupo:", grupo.nombre)
            addInfoRow(sheet, workbook, rowNum++, "Materia:", grupo.materia)
            addInfoRow(sheet, workbook, rowNum++, "Período:", "${formatDate(fechaInicio)} - ${formatDate(fechaFin)}")

            rowNum++ // Fila vacía

            // Encabezados de la tabla
            val headerRow = sheet.createRow(rowNum++)
            val headerStyle = createHeaderStyle(workbook)

            val cell0 = headerRow.createCell(0)
            cell0.setCellValue("N°")
            cell0.cellStyle = headerStyle

            val cell1 = headerRow.createCell(1)
            cell1.setCellValue("Fecha")
            cell1.cellStyle = headerStyle

            val cell2 = headerRow.createCell(2)
            cell2.setCellValue("Estado")
            cell2.cellStyle = headerStyle

            val cell3 = headerRow.createCell(3)
            cell3.setCellValue("Notas")
            cell3.cellStyle = headerStyle

            // Datos de asistencia
            registros.forEachIndexed { index, registro ->
                val dataRow = sheet.createRow(rowNum++)
                dataRow.createCell(0).setCellValue((index + 1).toDouble())
                dataRow.createCell(1).setCellValue(formatDate(registro.fecha))

                val estadoCell = dataRow.createCell(2)
                estadoCell.setCellValue(getEstadoText(registro.estado))
                estadoCell.cellStyle = createEstadoStyle(workbook, registro.estado)

                dataRow.createCell(3).setCellValue(registro.notas ?: "")
            }

            rowNum++ // Fila vacía

            // Estadísticas
            val stats = calculateStatistics(registros)
            val statsStyle = createBoldStyle(workbook)

            addStatsRow(sheet, workbook, rowNum++, "Total de clases:", stats.total.toString(), statsStyle)
            addStatsRow(sheet, workbook, rowNum++, "Presentes:", stats.presentes.toString(), statsStyle)
            addStatsRow(sheet, workbook, rowNum++, "Ausentes:", stats.ausentes.toString(), statsStyle)
            addStatsRow(sheet, workbook, rowNum++, "Tardanzas:", stats.tardanzas.toString(), statsStyle)
            addStatsRow(sheet, workbook, rowNum++, "Justificados:", stats.justificados.toString(), statsStyle)
            addStatsRow(sheet, workbook, rowNum++, "Porcentaje:", String.format("%.2f%%", stats.porcentaje), statsStyle)

            // Autoajustar columnas
            for (i in 0..3) {
                sheet.autoSizeColumn(i)
            }

            // Guardar archivo
            val fileName = "reporte_${estudiante.codigo}_${System.currentTimeMillis()}.xlsx"
            val file = File(getReportsDirectory(), fileName)

            FileOutputStream(file).use { outputStream ->
                workbook.write(outputStream)
            }

            return file

        } finally {
            workbook.close()
        }
    }

    /**
     * Genera reporte Excel de asistencia para un grupo completo
     */
    fun generateGroupAttendanceReport(
        grupo: Grupo,
        estudiantes: List<Estudiante>,
        registrosPorEstudiante: Map<Long, List<RegistroAsistencia>>,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): File {
        val workbook = XSSFWorkbook()

        try {
            val sheet = workbook.createSheet("Asistencia Grupo")

            var rowNum = 0

            // Título
            val titleRow = sheet.createRow(rowNum++)
            val titleCell = titleRow.createCell(0)
            titleCell.setCellValue("REPORTE DE ASISTENCIA DEL GRUPO")
            titleCell.cellStyle = createTitleStyle(workbook)
            sheet.addMergedRegion(org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 7))

            rowNum++

            // Información del grupo
            addInfoRow(sheet, workbook, rowNum++, "Grupo:", grupo.nombre)
            addInfoRow(sheet, workbook, rowNum++, "Materia:", grupo.materia)
            addInfoRow(sheet, workbook, rowNum++, "Horario:", grupo.horario ?: "N/A")
            addInfoRow(sheet, workbook, rowNum++, "Período:", "${formatDate(fechaInicio)} - ${formatDate(fechaFin)}")

            rowNum++

            // Encabezados
            val headerRow = sheet.createRow(rowNum++)
            val headerStyle = createHeaderStyle(workbook)
            val headers = listOf("N°", "Estudiante", "Código", "Total", "Presentes", "Ausentes", "Tardanzas", "Porcentaje")
            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
                cell.cellStyle = headerStyle
            }

            // Datos de estudiantes
            estudiantes.forEachIndexed { index, estudiante ->
                val registros = registrosPorEstudiante[estudiante.id] ?: emptyList()
                val stats = calculateStatistics(registros)

                val dataRow = sheet.createRow(rowNum++)
                dataRow.createCell(0).setCellValue((index + 1).toDouble())
                dataRow.createCell(1).setCellValue("${estudiante.nombre} ${estudiante.apellido}")
                dataRow.createCell(2).setCellValue(estudiante.codigo)
                dataRow.createCell(3).setCellValue(stats.total.toDouble())
                dataRow.createCell(4).setCellValue(stats.presentes.toDouble())
                dataRow.createCell(5).setCellValue(stats.ausentes.toDouble())
                dataRow.createCell(6).setCellValue(stats.tardanzas.toDouble())
                dataRow.createCell(7).setCellValue(String.format("%.1f%%", stats.porcentaje))
            }

            rowNum++

            // Estadísticas generales
            val statsTitle = sheet.createRow(rowNum++)
            val statsTitleCell = statsTitle.createCell(0)
            statsTitleCell.setCellValue("ESTADÍSTICAS GENERALES")
            statsTitleCell.cellStyle = createBoldStyle(workbook)

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

            val boldStyle = createBoldStyle(workbook)
            addStatsRow(sheet, workbook, rowNum++, "Total estudiantes:", estudiantes.size.toString(), boldStyle)
            addStatsRow(sheet, workbook, rowNum++, "Total registros:", totalRegistros.toString(), boldStyle)
            addStatsRow(sheet, workbook, rowNum++, "Total presentes:", totalPresentes.toString(), boldStyle)
            addStatsRow(sheet, workbook, rowNum++, "Total ausentes:", totalAusentes.toString(), boldStyle)
            addStatsRow(sheet, workbook, rowNum++, "Porcentaje promedio:", String.format("%.2f%%", porcentajeGeneral), boldStyle)

            // Autoajustar columnas
            for (i in 0..7) {
                sheet.autoSizeColumn(i)
            }

            // Guardar archivo
            val fileName = "reporte_grupo_${grupo.id}_${System.currentTimeMillis()}.xlsx"
            val file = File(getReportsDirectory(), fileName)

            FileOutputStream(file).use { outputStream ->
                workbook.write(outputStream)
            }

            return file

        } finally {
            workbook.close()
        }
    }

    /**
     * Agrega una fila de información
     */
    private fun addInfoRow(sheet: Sheet, workbook: XSSFWorkbook, rowNum: Int, label: String, value: String) {
        val row = sheet.createRow(rowNum)
        val labelCell = row.createCell(0)
        labelCell.setCellValue(label)
        labelCell.cellStyle = createBoldStyle(workbook)
        row.createCell(1).setCellValue(value)
    }

    /**
     * Agrega una fila de estadísticas
     */
    private fun addStatsRow(sheet: Sheet, workbook: XSSFWorkbook, rowNum: Int, label: String, value: String, style: XSSFCellStyle) {
        val row = sheet.createRow(rowNum)
        val cell0 = row.createCell(0)
        cell0.setCellValue(label)
        cell0.cellStyle = style
        row.createCell(1).setCellValue(value)
    }

    /**
     * Crea estilo para título
     */
    private fun createTitleStyle(workbook: XSSFWorkbook): XSSFCellStyle {
        val style = workbook.createCellStyle()
        val font = workbook.createFont()
        font.bold = true
        font.fontHeightInPoints = 16
        style.setFont(font)
        style.alignment = HorizontalAlignment.CENTER
        return style
    }

    /**
     * Crea estilo para encabezados
     */
    private fun createHeaderStyle(workbook: XSSFWorkbook): XSSFCellStyle {
        val style = workbook.createCellStyle()
        val font = workbook.createFont()
        font.bold = true
        font.color = IndexedColors.WHITE.index
        style.setFont(font)
        style.fillForegroundColor = IndexedColors.DARK_BLUE.index
        style.fillPattern = FillPatternType.SOLID_FOREGROUND
        style.alignment = HorizontalAlignment.CENTER
        style.borderBottom = BorderStyle.THIN
        style.borderTop = BorderStyle.THIN
        style.borderLeft = BorderStyle.THIN
        style.borderRight = BorderStyle.THIN
        return style
    }

    /**
     * Crea estilo en negrita
     */
    private fun createBoldStyle(workbook: XSSFWorkbook): XSSFCellStyle {
        val style = workbook.createCellStyle()
        val font = workbook.createFont()
        font.bold = true
        style.setFont(font)
        return style
    }

    /**
     * Crea estilo según el estado de asistencia
     */
    private fun createEstadoStyle(workbook: XSSFWorkbook, estado: EstadoAsistencia): XSSFCellStyle {
        val style = workbook.createCellStyle()

        val color = when (estado) {
            EstadoAsistencia.PRESENTE -> IndexedColors.LIGHT_GREEN
            EstadoAsistencia.AUSENTE -> IndexedColors.LIGHT_ORANGE
            EstadoAsistencia.TARDANZA -> IndexedColors.LIGHT_YELLOW
            EstadoAsistencia.JUSTIFICADO -> IndexedColors.LIGHT_BLUE
        }

        style.fillForegroundColor = color.index
        style.fillPattern = FillPatternType.SOLID_FOREGROUND
        style.alignment = HorizontalAlignment.CENTER

        return style
    }

    /**
     * Obtiene el texto del estado
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
     * Formatea una fecha
     */
    private fun formatDate(timestamp: Long): String {
        return DateUtils.formatDate(timestamp, "dd/MM/yyyy")
    }

    private fun formatDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    /**
     * Calcula estadísticas
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
     * Clase de datos para estadísticas
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
