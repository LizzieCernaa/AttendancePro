package sv.edu.catolica.asistedocente.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad que representa un registro de asistencia en la base de datos
 * Almacena el estado de asistencia de un estudiante en una fecha específica
 */
@Entity(
    tableName = "registros_asistencia",
    foreignKeys = [
        ForeignKey(
            entity = Estudiante::class,
            parentColumns = ["id"],
            childColumns = ["estudianteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Grupo::class,
            parentColumns = ["id"],
            childColumns = ["grupoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["estudianteId"]),
        Index(value = ["grupoId"]),
        Index(value = ["fecha"]),
        // Índice único para evitar registros duplicados del mismo estudiante en la misma fecha
        Index(value = ["estudianteId", "fecha"], unique = true)
    ]
)
data class RegistroAsistencia(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val estudianteId: Long,
    val grupoId: Long,
    val fecha: Long, // Timestamp de la fecha (sin hora)
    val estado: EstadoAsistencia,
    val notas: String? = null,
    val horaRegistro: Long = System.currentTimeMillis()
)
