package sv.edu.catolica.asistedocente.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import sv.edu.catolica.asistedocente.data.local.entities.Grupo
import sv.edu.catolica.asistedocente.data.repository.DocenteRepository
import sv.edu.catolica.asistedocente.data.repository.EstudianteRepository
import sv.edu.catolica.asistedocente.data.repository.GrupoRepository
import sv.edu.catolica.asistedocente.utils.AuthHelper
import sv.edu.catolica.asistedocente.utils.SampleDataGenerator
import javax.inject.Inject

/**
 * ViewModel para la pantalla de inicio/Dashboard
 * Gestiona la lista de grupos activos y estadísticas generales
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val grupoRepository: GrupoRepository,
    private val docenteRepository: DocenteRepository,
    private val estudianteRepository: EstudianteRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val docenteId: Long = AuthHelper.getDocenteId(application.applicationContext) ?: -1L

    init {
        checkAndLoadSampleData()
        loadGrupos()
    }

    /**
     * Verifica si la BD está vacía y carga datos de ejemplo
     */
    private fun checkAndLoadSampleData() {
        viewModelScope.launch {
            try {
                val docentes = docenteRepository.getAllDocentes().firstOrNull()
                if (docentes.isNullOrEmpty()) {
                    // Base de datos vacía, cargar datos de ejemplo
                    SampleDataGenerator.generateSampleData(
                        docenteRepository,
                        grupoRepository,
                        estudianteRepository
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Carga los grupos activos del docente logueado
     */
    private fun loadGrupos() {
        viewModelScope.launch {
            try {
                grupoRepository.getGruposByDocente(docenteId).collect { grupos ->
                    if (grupos.isEmpty()) {
                        _uiState.value = HomeUiState.Empty
                    } else {
                        _uiState.value = HomeUiState.Success(grupos)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Recarga los grupos
     */
    fun refresh() {
        _uiState.value = HomeUiState.Loading
        loadGrupos()
    }

    /**
     * Elimina un grupo (lo marca como inactivo)
     */
    fun deleteGrupo(grupoId: Long) {
        viewModelScope.launch {
            try {
                grupoRepository.deleteGrupo(grupoId)
            } catch (e: Exception) {
                // TODO: Manejar error
            }
        }
    }
}

/**
 * Estados posibles de la pantalla Home
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Success(val grupos: List<Grupo>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
