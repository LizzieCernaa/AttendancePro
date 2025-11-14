package sv.edu.catolica.asistedocente.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import sv.edu.catolica.asistedocente.data.local.entities.Grupo

/**
 * Data Access Object para operaciones de Grupo en la base de datos
 */
@Dao
interface GrupoDao {

    @Query("SELECT * FROM grupos WHERE activo = 1 ORDER BY nombre")
    fun getAllGrupos(): Flow<List<Grupo>>

    @Query("SELECT * FROM grupos WHERE id = :id")
    fun getGrupoById(id: Long): Flow<Grupo?>

    @Query("SELECT * FROM grupos WHERE id = :id")
    suspend fun getGrupoByIdSync(id: Long): Grupo?

    @Query("SELECT * FROM grupos WHERE docenteId = :docenteId AND activo = 1 ORDER BY nombre")
    fun getGruposByDocente(docenteId: Long): Flow<List<Grupo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrupo(grupo: Grupo): Long

    @Update
    suspend fun updateGrupo(grupo: Grupo)

    @Delete
    suspend fun deleteGrupo(grupo: Grupo)

    @Query("UPDATE grupos SET activo = 0 WHERE id = :id")
    suspend fun desactivarGrupo(id: Long)

    @Query("SELECT COUNT(*) FROM grupos WHERE activo = 1")
    suspend fun getGruposCount(): Int

    @Query("SELECT COUNT(*) FROM grupos WHERE docenteId = :docenteId AND activo = 1")
    suspend fun getGruposCountByDocente(docenteId: Long): Int
}
