package sv.edu.catolica.asistedocente.data.repository

import kotlinx.coroutines.flow.Flow
import sv.edu.catolica.asistedocente.data.local.dao.RegistroAsistenciaDao
import sv.edu.catolica.asistedocente.data.local.entities.EstadoAsistencia
import sv.edu.catolica.asistedocente.data.local.entities.RegistroAsistencia
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para gestionar operaciones relacionadas con Registros de Asistencia
 * Abstrae la fuente de datos y proporciona una API limpia para la capa de presentación
 */
@Singleton
class AsistenciaRepository @Inject constructor(
    private val registroAsistenciaDao: RegistroAsistenciaDao
) {

    fun getAllRegistros(): Flow<List<RegistroAsistencia>> {
        return registroAsistenciaDao.getAllRegistros()
    }

    fun getRegistroById(id: Long): Flow<RegistroAsistencia?> {
        return registroAsistenciaDao.getRegistroById(id)
    }

    fun getRegistrosByEstudiante(estudianteId: Long): Flow<List<RegistroAsistencia>> {
        return registroAsistenciaDao.getRegistrosByEstudiante(estudianteId)
    }

    fun getRegistrosByGrupo(grupoId: Long): Flow<List<RegistroAsistencia>> {
        return registroAsistenciaDao.getRegistrosByGrupo(grupoId)
    }

    fun getRegistrosByGrupoAndFecha(grupoId: Long, fecha: Long): Flow<List<RegistroAsistencia>> {
        return registroAsistenciaDao.getRegistrosByGrupoAndFecha(grupoId, fecha)
    }

    suspend fun getRegistrosByGrupoAndFechaSync(grupoId: Long, fecha: Long): List<RegistroAsistencia> {
        return registroAsistenciaDao.getRegistrosByGrupoAndFechaSync(grupoId, fecha)
    }

    suspend fun getRegistroByEstudianteAndFecha(estudianteId: Long, fecha: Long): RegistroAsistencia? {
        return registroAsistenciaDao.getRegistroByEstudianteAndFecha(estudianteId, fecha)
    }

    fun getRegistrosByGrupoAndRangoFechas(grupoId: Long, fechaInicio: Long, fechaFin: Long): Flow<List<RegistroAsistencia>> {
        return registroAsistenciaDao.getRegistrosByGrupoAndRangoFechas(grupoId, fechaInicio, fechaFin)
    }

    suspend fun getRegistrosByGrupoAndDateRange(grupoId: Long, fechaInicio: Long, fechaFin: Long): List<RegistroAsistencia> {
        return registroAsistenciaDao.getRegistrosByGrupoAndRangoFechasSync(grupoId, fechaInicio, fechaFin)
    }

    fun getRegistrosByEstudianteAndRangoFechas(estudianteId: Long, fechaInicio: Long, fechaFin: Long): Flow<List<RegistroAsistencia>> {
        return registroAsistenciaDao.getRegistrosByEstudianteAndRangoFechas(estudianteId, fechaInicio, fechaFin)
    }

    suspend fun getCountByEstudianteAndEstado(estudianteId: Long, estado: EstadoAsistencia): Int {
        return registroAsistenciaDao.getCountByEstudianteAndEstado(estudianteId, estado)
    }

    suspend fun getCountByEstudianteAndEstadoAndRangoFechas(
        estudianteId: Long,
        estado: EstadoAsistencia,
        fechaInicio: Long,
        fechaFin: Long
    ): Int {
        return registroAsistenciaDao.getCountByEstudianteAndEstadoAndRangoFechas(
            estudianteId, estado, fechaInicio, fechaFin
        )
    }

    suspend fun getTotalRegistrosByEstudianteAndRangoFechas(estudianteId: Long, fechaInicio: Long, fechaFin: Long): Int {
        return registroAsistenciaDao.getTotalRegistrosByEstudianteAndRangoFechas(estudianteId, fechaInicio, fechaFin)
    }

    fun getFechasConRegistrosByGrupo(grupoId: Long): Flow<List<Long>> {
        return registroAsistenciaDao.getFechasConRegistrosByGrupo(grupoId)
    }

    suspend fun insertRegistro(registro: RegistroAsistencia): Long {
        return registroAsistenciaDao.insertRegistro(registro)
    }

    suspend fun insertRegistros(registros: List<RegistroAsistencia>) {
        registroAsistenciaDao.insertRegistros(registros)
    }

    suspend fun updateRegistro(registro: RegistroAsistencia) {
        registroAsistenciaDao.updateRegistro(registro)
    }

    suspend fun deleteRegistro(registro: RegistroAsistencia) {
        registroAsistenciaDao.deleteRegistro(registro)
    }

    suspend fun deleteRegistrosByGrupoAndFecha(grupoId: Long, fecha: Long) {
        registroAsistenciaDao.deleteRegistrosByGrupoAndFecha(grupoId, fecha)
    }

    suspend fun deleteRegistrosByEstudiante(estudianteId: Long) {
        registroAsistenciaDao.deleteRegistrosByEstudiante(estudianteId)
    }

    /**
     * Calcula el porcentaje de asistencia de un estudiante en un rango de fechas
     */
    suspend fun calcularPorcentajeAsistencia(estudianteId: Long, fechaInicio: Long, fechaFin: Long): Float {
        val totalRegistros = getTotalRegistrosByEstudianteAndRangoFechas(estudianteId, fechaInicio, fechaFin)
        if (totalRegistros == 0) return 0f

        val presentes = getCountByEstudianteAndEstadoAndRangoFechas(estudianteId, EstadoAsistencia.PRESENTE, fechaInicio, fechaFin)
        return (presentes.toFloat() / totalRegistros.toFloat()) * 100f
    }
}
