package sv.edu.catolica.asistedocente.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import sv.edu.catolica.asistedocente.data.local.dao.DocenteDao
import sv.edu.catolica.asistedocente.data.local.dao.EstudianteDao
import sv.edu.catolica.asistedocente.data.local.dao.GrupoDao
import sv.edu.catolica.asistedocente.data.local.dao.RegistroAsistenciaDao
import sv.edu.catolica.asistedocente.data.local.database.AppDatabase
import sv.edu.catolica.asistedocente.data.local.entities.Docente
import sv.edu.catolica.asistedocente.data.local.entities.Estudiante
import sv.edu.catolica.asistedocente.data.local.entities.Grupo
import javax.inject.Singleton

/**
 * Módulo de Hilt que proporciona dependencias relacionadas con la base de datos
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // Solo para desarrollo, remover en producción
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Insertar datos de ejemplo cuando se crea la base de datos
                    CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                        populateDatabase(context)
                    }
                }
            })
            .build()
    }

    /**
     * Puebla la base de datos con datos de ejemplo
     */
    private suspend fun populateDatabase(context: Context) {
        val database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()

        val docenteDao = database.docenteDao()
        val grupoDao = database.grupoDao()
        val estudianteDao = database.estudianteDao()

        try {
            // Insertar docente María
            val mariaId = docenteDao.insertDocente(
                Docente(
                    nombre = "María",
                    apellido = "García",
                    email = "maria.garcia@catolica.edu.sv",
                    password = "1234",
                    telefono = "7890-1234",
                    foto = null,
                    activo = true,
                    fechaCreacion = System.currentTimeMillis()
                )
            )

            // Insertar grupos con diferentes materias
            val matemId = grupoDao.insertGrupo(
                Grupo(
                    nombre = "Matemáticas 101",
                    materia = "Matemáticas",
                    horario = "Lunes y Miércoles 8:00-10:00",
                    descripcion = "Curso básico de matemáticas",
                    docenteId = mariaId,
                    activo = true,
                    fechaCreacion = System.currentTimeMillis()
                )
            )

            val fisicaId = grupoDao.insertGrupo(
                Grupo(
                    nombre = "Física General",
                    materia = "Física",
                    horario = "Martes y Jueves 10:00-12:00",
                    descripcion = "Introducción a la física",
                    docenteId = mariaId,
                    activo = true,
                    fechaCreacion = System.currentTimeMillis()
                )
            )

            val progId = grupoDao.insertGrupo(
                Grupo(
                    nombre = "Programación I",
                    materia = "Programación",
                    horario = "Viernes 14:00-18:00",
                    descripcion = "Fundamentos de programación",
                    docenteId = mariaId,
                    activo = true,
                    fechaCreacion = System.currentTimeMillis()
                )
            )

            // Insertar estudiantes en Matemáticas
            estudianteDao.insertEstudiante(
                Estudiante(
                    nombre = "Juan",
                    apellido = "Pérez",
                    codigo = "2024001",
                    email = "juan.perez@example.com",
                    foto = null,
                    grupoId = matemId,
                    activo = true,
                    fechaCreacion = System.currentTimeMillis()
                )
            )

            estudianteDao.insertEstudiante(
                Estudiante(
                    nombre = "Ana",
                    apellido = "Martínez",
                    codigo = "2024002",
                    email = "ana.martinez@example.com",
                    foto = null,
                    grupoId = matemId,
                    activo = true,
                    fechaCreacion = System.currentTimeMillis()
                )
            )

            estudianteDao.insertEstudiante(
                Estudiante(
                    nombre = "Carlos",
                    apellido = "López",
                    codigo = "2024003",
                    email = "carlos.lopez@example.com",
                    foto = null,
                    grupoId = matemId,
                    activo = true,
                    fechaCreacion = System.currentTimeMillis()
                )
            )

            // Insertar estudiantes en Física
            estudianteDao.insertEstudiante(
                Estudiante(
                    nombre = "Laura",
                    apellido = "Hernández",
                    codigo = "2024004",
                    email = "laura.hernandez@example.com",
                    foto = null,
                    grupoId = fisicaId,
                    activo = true,
                    fechaCreacion = System.currentTimeMillis()
                )
            )

            estudianteDao.insertEstudiante(
                Estudiante(
                    nombre = "Pedro",
                    apellido = "Ramírez",
                    codigo = "2024005",
                    email = "pedro.ramirez@example.com",
                    foto = null,
                    grupoId = fisicaId,
                    activo = true,
                    fechaCreacion = System.currentTimeMillis()
                )
            )

            // Insertar estudiantes en Programación
            estudianteDao.insertEstudiante(
                Estudiante(
                    nombre = "Sofia",
                    apellido = "Torres",
                    codigo = "2024006",
                    email = "sofia.torres@example.com",
                    foto = null,
                    grupoId = progId,
                    activo = true,
                    fechaCreacion = System.currentTimeMillis()
                )
            )

            estudianteDao.insertEstudiante(
                Estudiante(
                    nombre = "Miguel",
                    apellido = "Flores",
                    codigo = "2024007",
                    email = "miguel.flores@example.com",
                    foto = null,
                    grupoId = progId,
                    activo = true,
                    fechaCreacion = System.currentTimeMillis()
                )
            )

            estudianteDao.insertEstudiante(
                Estudiante(
                    nombre = "Valentina",
                    apellido = "Morales",
                    codigo = "2024008",
                    email = "valentina.morales@example.com",
                    foto = null,
                    grupoId = progId,
                    activo = true,
                    fechaCreacion = System.currentTimeMillis()
                )
            )

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            database.close()
        }
    }

    @Provides
    @Singleton
    fun provideDocenteDao(database: AppDatabase): DocenteDao {
        return database.docenteDao()
    }

    @Provides
    @Singleton
    fun provideGrupoDao(database: AppDatabase): GrupoDao {
        return database.grupoDao()
    }

    @Provides
    @Singleton
    fun provideEstudianteDao(database: AppDatabase): EstudianteDao {
        return database.estudianteDao()
    }

    @Provides
    @Singleton
    fun provideRegistroAsistenciaDao(database: AppDatabase): RegistroAsistenciaDao {
        return database.registroAsistenciaDao()
    }
}
