package sv.edu.catolica.asistedocente.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import sv.edu.catolica.asistedocente.data.local.entities.EstadoAsistencia
import sv.edu.catolica.asistedocente.data.local.entities.RegistroAsistencia

/**
 * Data Access Object para operaciones de Registro de Asistencia en la base de datos
 */
@Dao
interface RegistroAsistenciaDao {

    @Query("SELECT * FROM registros_asistencia ORDER BY fecha DESC, horaRegistro DESC")
    fun getAllRegistros(): Flow<List<RegistroAsistencia>>

    @Query("SELECT * FROM registros_asistencia WHERE id = :id")
    fun getRegistroById(id: Long): Flow<RegistroAsistencia?>

    @Query("SELECT * FROM registros_asistencia WHERE estudianteId = :estudianteId ORDER BY fecha DESC")
    fun getRegistrosByEstudiante(estudianteId: Long): Flow<List<RegistroAsistencia>>

    @Query("SELECT * FROM registros_asistencia WHERE grupoId = :grupoId ORDER BY fecha DESC")
    fun getRegistrosByGrupo(grupoId: Long): Flow<List<RegistroAsistencia>>

    @Query("SELECT * FROM registros_asistencia WHERE grupoId = :grupoId AND fecha = :fecha")
    fun getRegistrosByGrupoAndFecha(grupoId: Long, fecha: Long): Flow<List<RegistroAsistencia>>

    @Query("SELECT * FROM registros_asistencia WHERE grupoId = :grupoId AND fecha = :fecha")
    suspend fun getRegistrosByGrupoAndFechaSync(grupoId: Long, fecha: Long): List<RegistroAsistencia>

    @Query("SELECT * FROM registros_asistencia WHERE estudianteId = :estudianteId AND fecha = :fecha")
    suspend fun getRegistroByEstudianteAndFecha(estudianteId: Long, fecha: Long): RegistroAsistencia?

    @Query("SELECT * FROM registros_asistencia WHERE grupoId = :grupoId AND fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    fun getRegistrosByGrupoAndRangoFechas(grupoId: Long, fechaInicio: Long, fechaFin: Long): Flow<List<RegistroAsistencia>>

    @Query("SELECT * FROM registros_asistencia WHERE grupoId = :grupoId AND fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    suspend fun getRegistrosByGrupoAndRangoFechasSync(grupoId: Long, fechaInicio: Long, fechaFin: Long): List<RegistroAsistencia>

    @Query("SELECT * FROM registros_asistencia WHERE estudianteId = :estudianteId AND fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    fun getRegistrosByEstudianteAndRangoFechas(estudianteId: Long, fechaInicio: Long, fechaFin: Long): Flow<List<RegistroAsistencia>>

    @Query("SELECT COUNT(*) FROM registros_asistencia WHERE estudianteId = :estudianteId AND estado = :estado")
    suspend fun getCountByEstudianteAndEstado(estudianteId: Long, estado: EstadoAsistencia): Int

    @Query("SELECT COUNT(*) FROM registros_asistencia WHERE estudianteId = :estudianteId AND fecha BETWEEN :fechaInicio AND :fechaFin AND estado = :estado")
    suspend fun getCountByEstudianteAndEstadoAndRangoFechas(
        estudianteId: Long,
        estado: EstadoAsistencia,
        fechaInicio: Long,
        fechaFin: Long
    ): Int

    @Query("SELECT COUNT(*) FROM registros_asistencia WHERE estudianteId = :estudianteId AND fecha BETWEEN :fechaInicio AND :fechaFin")
    suspend fun getTotalRegistrosByEstudianteAndRangoFechas(estudianteId: Long, fechaInicio: Long, fechaFin: Long): Int

    @Query("SELECT DISTINCT fecha FROM registros_asistencia WHERE grupoId = :grupoId ORDER BY fecha DESC")
    fun getFechasConRegistrosByGrupo(grupoId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistro(registro: RegistroAsistencia): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistros(registros: List<RegistroAsistencia>)

    @Update
    suspend fun updateRegistro(registro: RegistroAsistencia)

    @Delete
    suspend fun deleteRegistro(registro: RegistroAsistencia)

    @Query("DELETE FROM registros_asistencia WHERE grupoId = :grupoId AND fecha = :fecha")
    suspend fun deleteRegistrosByGrupoAndFecha(grupoId: Long, fecha: Long)

    @Query("DELETE FROM registros_asistencia WHERE estudianteId = :estudianteId")
    suspend fun deleteRegistrosByEstudiante(estudianteId: Long)
}
