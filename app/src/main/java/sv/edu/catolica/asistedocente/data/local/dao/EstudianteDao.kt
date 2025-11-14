package sv.edu.catolica.asistedocente.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import sv.edu.catolica.asistedocente.data.local.entities.Estudiante

/**
 * Data Access Object para operaciones de Estudiante en la base de datos
 */
@Dao
interface EstudianteDao {

    @Query("SELECT * FROM estudiantes WHERE activo = 1 ORDER BY apellido, nombre")
    fun getAllEstudiantes(): Flow<List<Estudiante>>

    @Query("SELECT * FROM estudiantes WHERE id = :id")
    fun getEstudianteById(id: Long): Flow<Estudiante?>

    @Query("SELECT * FROM estudiantes WHERE id = :id")
    suspend fun getEstudianteByIdSync(id: Long): Estudiante?

    @Query("SELECT * FROM estudiantes WHERE grupoId = :grupoId AND activo = 1 ORDER BY apellido, nombre")
    fun getEstudiantesByGrupo(grupoId: Long): Flow<List<Estudiante>>

    @Query("SELECT * FROM estudiantes WHERE grupoId = :grupoId AND activo = 1 ORDER BY apellido, nombre")
    suspend fun getEstudiantesByGrupoSync(grupoId: Long): List<Estudiante>

    @Query("SELECT * FROM estudiantes WHERE codigo = :codigo")
    suspend fun getEstudianteByCodigo(codigo: String): Estudiante?

    @Query("SELECT * FROM estudiantes WHERE (nombre LIKE '%' || :query || '%' OR apellido LIKE '%' || :query || '%' OR codigo LIKE '%' || :query || '%') AND activo = 1 ORDER BY apellido, nombre")
    fun searchEstudiantes(query: String): Flow<List<Estudiante>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstudiante(estudiante: Estudiante): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstudiantes(estudiantes: List<Estudiante>)

    @Update
    suspend fun updateEstudiante(estudiante: Estudiante)

    @Delete
    suspend fun deleteEstudiante(estudiante: Estudiante)

    @Query("UPDATE estudiantes SET activo = 0 WHERE id = :id")
    suspend fun desactivarEstudiante(id: Long)

    @Query("UPDATE estudiantes SET grupoId = :nuevoGrupoId WHERE id = :estudianteId")
    suspend fun transferirEstudiante(estudianteId: Long, nuevoGrupoId: Long)

    @Query("SELECT COUNT(*) FROM estudiantes WHERE grupoId = :grupoId AND activo = 1")
    suspend fun getEstudiantesCountByGrupo(grupoId: Long): Int

    @Query("SELECT COUNT(*) FROM estudiantes WHERE activo = 1")
    suspend fun getEstudiantesCount(): Int
}
