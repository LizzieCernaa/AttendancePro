package sv.edu.catolica.asistedocente.data.repository

import kotlinx.coroutines.flow.Flow
import sv.edu.catolica.asistedocente.data.local.dao.DocenteDao
import sv.edu.catolica.asistedocente.data.local.entities.Docente
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para gestionar operaciones relacionadas con Docentes
 * Abstrae la fuente de datos y proporciona una API limpia para la capa de presentación
 */
@Singleton
class DocenteRepository @Inject constructor(
    private val docenteDao: DocenteDao
) {

    fun getAllDocentes(): Flow<List<Docente>> {
        return docenteDao.getAllDocentes()
    }

    fun getDocenteById(id: Long): Flow<Docente?> {
        return docenteDao.getDocenteById(id)
    }

    suspend fun getDocenteByIdSync(id: Long): Docente? {
        return docenteDao.getDocenteByIdSync(id)
    }

    suspend fun insertDocente(docente: Docente): Long {
        return docenteDao.insertDocente(docente)
    }

    suspend fun updateDocente(docente: Docente) {
        docenteDao.updateDocente(docente)
    }

    suspend fun deleteDocente(docente: Docente) {
        docenteDao.deleteDocente(docente)
    }

    suspend fun desactivarDocente(id: Long) {
        docenteDao.desactivarDocente(id)
    }

    suspend fun getDocentesCount(): Int {
        return docenteDao.getDocentesCount()
    }
}
