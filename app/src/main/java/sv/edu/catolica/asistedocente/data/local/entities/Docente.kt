package sv.edu.catolica.asistedocente.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa un docente en la base de datos
 */
@Entity(tableName = "docentes")
data class Docente(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val apellido: String,
    val email: String = "",
    val password: String = "", // Contraseña del docente
    val telefono: String? = null,
    val foto: String? = null, // URI de la foto
    val activo: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis()
)
