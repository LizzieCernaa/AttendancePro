package sv.edu.catolica.asistedocente.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad que representa un grupo o clase en la base de datos
 */
@Entity(
    tableName = "grupos",
    foreignKeys = [
        ForeignKey(
            entity = Docente::class,
            parentColumns = ["id"],
            childColumns = ["docenteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["docenteId"])]
)
data class Grupo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val materia: String,
    val horario: String? = null,
    val docenteId: Long,
    val activo: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val descripcion: String? = null
)
