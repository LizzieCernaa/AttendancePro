package sv.edu.catolica.asistedocente.data.repository

import kotlinx.coroutines.flow.Flow
import sv.edu.catolica.asistedocente.data.local.dao.EstudianteDao
import sv.edu.catolica.asistedocente.data.local.entities.Estudiante
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para gestionar operaciones relacionadas con Estudiantes
 * Abstrae la fuente de datos y proporciona una API limpia para la capa de presentación
 */
@Singleton
class EstudianteRepository @Inject constructor(
    private val estudianteDao: EstudianteDao
) {

    fun getAllEstudiantes(): Flow<List<Estudiante>> {
        return estudianteDao.getAllEstudiantes()
    }

    fun getEstudianteById(id: Long): Flow<Estudiante?> {
        return estudianteDao.getEstudianteById(id)
    }

    suspend fun getEstudianteByIdSync(id: Long): Estudiante? {
        return estudianteDao.getEstudianteByIdSync(id)
    }

    fun getEstudiantesByGrupo(grupoId: Long): Flow<List<Estudiante>> {
        return estudianteDao.getEstudiantesByGrupo(grupoId)
    }

    suspend fun getEstudiantesByGrupoSync(grupoId: Long): List<Estudiante> {
        return estudianteDao.getEstudiantesByGrupoSync(grupoId)
    }

    suspend fun getEstudianteByCodigo(codigo: String): Estudiante? {
        return estudianteDao.getEstudianteByCodigo(codigo)
    }

    fun searchEstudiantes(query: String): Flow<List<Estudiante>> {
        return estudianteDao.searchEstudiantes(query)
    }

    suspend fun insertEstudiante(estudiante: Estudiante): Long {
        return estudianteDao.insertEstudiante(estudiante)
    }

    suspend fun insertEstudiantes(estudiantes: List<Estudiante>) {
        estudianteDao.insertEstudiantes(estudiantes)
    }

    suspend fun updateEstudiante(estudiante: Estudiante) {
        estudianteDao.updateEstudiante(estudiante)
    }

    suspend fun deleteEstudiante(estudiante: Estudiante) {
        estudianteDao.deleteEstudiante(estudiante)
    }

    suspend fun desactivarEstudiante(id: Long) {
        estudianteDao.desactivarEstudiante(id)
    }

    suspend fun transferirEstudiante(estudianteId: Long, nuevoGrupoId: Long) {
        estudianteDao.transferirEstudiante(estudianteId, nuevoGrupoId)
    }

    suspend fun getEstudiantesCountByGrupo(grupoId: Long): Int {
        return estudianteDao.getEstudiantesCountByGrupo(grupoId)
    }

    suspend fun getEstudiantesCount(): Int {
        return estudianteDao.getEstudiantesCount()
    }
}
