package sv.edu.catolica.asistedocente.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import sv.edu.catolica.asistedocente.data.local.dao.DocenteDao
import sv.edu.catolica.asistedocente.data.local.dao.EstudianteDao
import sv.edu.catolica.asistedocente.data.local.dao.GrupoDao
import sv.edu.catolica.asistedocente.data.local.dao.RegistroAsistenciaDao
import sv.edu.catolica.asistedocente.data.local.entities.Docente
import sv.edu.catolica.asistedocente.data.local.entities.Estudiante
import sv.edu.catolica.asistedocente.data.local.entities.Grupo
import sv.edu.catolica.asistedocente.data.local.entities.RegistroAsistencia

/**
 * Base de datos principal de la aplicación usando Room
 * Contiene todas las entidades y proporciona acceso a los DAOs
 */
@Database(
    entities = [
        Docente::class,
        Grupo::class,
        Estudiante::class,
        RegistroAsistencia::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun docenteDao(): DocenteDao
    abstract fun grupoDao(): GrupoDao
    abstract fun estudianteDao(): EstudianteDao
    abstract fun registroAsistenciaDao(): RegistroAsistenciaDao

    companion object {
        const val DATABASE_NAME = "asiste_docente_db"
    }
}
