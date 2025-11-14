package sv.edu.catolica.asistedocente.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sv.edu.catolica.asistedocente.data.local.dao.DocenteDao
import sv.edu.catolica.asistedocente.data.local.dao.EstudianteDao
import sv.edu.catolica.asistedocente.data.local.dao.GrupoDao
import sv.edu.catolica.asistedocente.data.local.dao.RegistroAsistenciaDao
import sv.edu.catolica.asistedocente.data.repository.AsistenciaRepository
import sv.edu.catolica.asistedocente.data.repository.DocenteRepository
import sv.edu.catolica.asistedocente.data.repository.EstudianteRepository
import sv.edu.catolica.asistedocente.data.repository.GrupoRepository
import javax.inject.Singleton

/**
 * Módulo de Hilt que proporciona dependencias relacionadas con los Repositories
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDocenteRepository(docenteDao: DocenteDao): DocenteRepository {
        return DocenteRepository(docenteDao)
    }

    @Provides
    @Singleton
    fun provideGrupoRepository(grupoDao: GrupoDao): GrupoRepository {
        return GrupoRepository(grupoDao)
    }

    @Provides
    @Singleton
    fun provideEstudianteRepository(estudianteDao: EstudianteDao): EstudianteRepository {
        return EstudianteRepository(estudianteDao)
    }

    @Provides
    @Singleton
    fun provideAsistenciaRepository(registroAsistenciaDao: RegistroAsistenciaDao): AsistenciaRepository {
        return AsistenciaRepository(registroAsistenciaDao)
    }
}
