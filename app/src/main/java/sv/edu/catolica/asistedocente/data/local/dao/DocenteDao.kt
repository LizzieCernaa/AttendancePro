package sv.edu.catolica.asistedocente.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import sv.edu.catolica.asistedocente.data.local.entities.Docente

/**
 * Data Access Object para operaciones de Docente en la base de datos
 */
@Dao
interface DocenteDao {

    @Query("SELECT * FROM docentes WHERE activo = 1 ORDER BY apellido, nombre")
    fun getAllDocentes(): Flow<List<Docente>>

    @Query("SELECT * FROM docentes WHERE id = :id")
    fun getDocenteById(id: Long): Flow<Docente?>

    @Query("SELECT * FROM docentes WHERE id = :id")
    suspend fun getDocenteByIdSync(id: Long): Docente?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocente(docente: Docente): Long

    @Update
    suspend fun updateDocente(docente: Docente)

    @Delete
    suspend fun deleteDocente(docente: Docente)

    @Query("UPDATE docentes SET activo = 0 WHERE id = :id")
    suspend fun desactivarDocente(id: Long)

    @Query("SELECT COUNT(*) FROM docentes WHERE activo = 1")
    suspend fun getDocentesCount(): Int
}
