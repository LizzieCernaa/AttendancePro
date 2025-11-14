package sv.edu.catolica.asistedocente.data.repository

import kotlinx.coroutines.flow.Flow
import sv.edu.catolica.asistedocente.data.local.dao.GrupoDao
import sv.edu.catolica.asistedocente.data.local.entities.Grupo
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para gestionar operaciones relacionadas con Grupos
 * Abstrae la fuente de datos y proporciona una API limpia para la capa de presentación
 */
@Singleton
class GrupoRepository @Inject constructor(
    private val grupoDao: GrupoDao
) {

    fun getAllGrupos(): Flow<List<Grupo>> {
        return grupoDao.getAllGrupos()
    }

    fun getAllGruposActivos(): Flow<List<Grupo>> {
        return grupoDao.getAllGrupos()
    }

    fun getGrupoById(id: Long): Flow<Grupo?> {
        return grupoDao.getGrupoById(id)
    }

    suspend fun getGrupoByIdSync(id: Long): Grupo? {
        return grupoDao.getGrupoByIdSync(id)
    }

    fun getGruposByDocente(docenteId: Long): Flow<List<Grupo>> {
        return grupoDao.getGruposByDocente(docenteId)
    }

    suspend fun insertGrupo(grupo: Grupo): Long {
        return grupoDao.insertGrupo(grupo)
    }

    suspend fun updateGrupo(grupo: Grupo) {
        grupoDao.updateGrupo(grupo)
    }

    suspend fun deleteGrupo(grupo: Grupo) {
        grupoDao.deleteGrupo(grupo)
    }

    suspend fun deleteGrupo(id: Long) {
        grupoDao.desactivarGrupo(id)
    }

    suspend fun desactivarGrupo(id: Long) {
        grupoDao.desactivarGrupo(id)
    }

    suspend fun getGruposCount(): Int {
        return grupoDao.getGruposCount()
    }

    suspend fun getGruposCountByDocente(docenteId: Long): Int {
        return grupoDao.getGruposCountByDocente(docenteId)
    }
}
