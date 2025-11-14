package sv.edu.catolica.asistedocente.ui.screens.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sv.edu.catolica.asistedocente.MainActivity
import sv.edu.catolica.asistedocente.utils.LocaleHelper
import javax.inject.Inject

/**
 * ViewModel para la pantalla de configuración
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadCurrentSettings()
    }

    /**
     * Carga la configuración actual
     */
    private fun loadCurrentSettings() {
        viewModelScope.launch {
            val currentLanguage = LocaleHelper.getLanguage(context)
            _uiState.update { it.copy(currentLanguage = currentLanguage) }
        }
    }

    /**
     * Cambia el idioma de la aplicación
     */
    fun changeLanguage(languageCode: String) {
        viewModelScope.launch {
            // Guardar el nuevo idioma en SharedPreferences
            LocaleHelper.setLocale(context, languageCode)

            // Aplicar el idioma usando AppCompatDelegate (Android moderno)
            val appLocale = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(appLocale)

            // Actualizar el estado
            _uiState.update {
                it.copy(
                    currentLanguage = languageCode,
                    showRestartDialog = false  // No necesitamos reiniciar con AppCompatDelegate
                )
            }
        }
    }

    /**
     * Reinicia la aplicación para aplicar el cambio de idioma
     */
    fun restartApp() {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        // Terminar el proceso actual
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    /**
     * Cierra el diálogo de reinicio sin reiniciar
     */
    fun dismissRestartDialog() {
        _uiState.update { it.copy(showRestartDialog = false) }
    }
}

/**
 * Estado de la UI de configuración
 */
data class SettingsUiState(
    val currentLanguage: String = "es",
    val showRestartDialog: Boolean = false
)
