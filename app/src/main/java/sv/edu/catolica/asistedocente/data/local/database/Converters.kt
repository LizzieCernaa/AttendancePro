package sv.edu.catolica.asistedocente.data.local.database

import androidx.room.TypeConverter
import sv.edu.catolica.asistedocente.data.local.entities.EstadoAsistencia

/**
 * Convertidores de tipo para Room Database
 * Permite a Room convertir tipos personalizados a tipos primitivos que puede almacenar
 */
class Converters {

    @TypeConverter
    fun fromEstadoAsistencia(estado: EstadoAsistencia): String {
        return estado.name
    }

    @TypeConverter
    fun toEstadoAsistencia(estadoString: String): EstadoAsistencia {
        return EstadoAsistencia.valueOf(estadoString)
    }
}
