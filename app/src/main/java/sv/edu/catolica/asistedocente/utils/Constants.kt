package sv.edu.catolica.asistedocente.utils

/**
 * Constantes utilizadas en toda la aplicación
 */
object Constants {

    // Formato de fechas
    const val DATE_FORMAT = "dd/MM/yyyy"
    const val TIME_FORMAT = "HH:mm"
    const val DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm"

    // Keys para navegación
    const val NAV_ARG_GRUPO_ID = "grupoId"
    const val NAV_ARG_ESTUDIANTE_ID = "estudianteId"
    const val NAV_ARG_FECHA = "fecha"

    // Valores por defecto
    const val DEFAULT_DOCENTE_ID = 1L

    // Límites
    const val MAX_NOMBRE_LENGTH = 100
    const val MAX_CODIGO_LENGTH = 20
    const val MAX_EMAIL_LENGTH = 100

    // Tipos de archivo
    const val PDF_MIME_TYPE = "application/pdf"
    const val EXCEL_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

    // Directorios
    const val REPORTS_DIR = "reportes"
    const val BACKUPS_DIR = "backups"
    const val PHOTOS_DIR = "fotos"
}
