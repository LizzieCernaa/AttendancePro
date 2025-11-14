package sv.edu.catolica.asistedocente.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad que representa un estudiante en la base de datos
 */
@Entity(
    tableName = "estudiantes",
    foreignKeys = [
        ForeignKey(
            entity = Grupo::class,
            parentColumns = ["id"],
            childColumns = ["grupoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["grupoId"]),
        Index(value = ["codigo"], unique = true)
    ]
)
data class Estudiante(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val apellido: String,
    val codigo: String, // Matrícula o código único
    val email: String? = null,
    val foto: String? = null, // URI de la foto
    val grupoId: Long,
    val activo: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis()
)
