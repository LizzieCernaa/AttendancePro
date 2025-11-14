package sv.edu.catolica.asistedocente

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.HiltAndroidApp
import sv.edu.catolica.asistedocente.utils.LocaleHelper
import java.util.Locale

/**
 * Clase Application principal de la aplicación
 * Anotada con @HiltAndroidApp para habilitar la inyección de dependencias de Hilt
 */
@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Aplicar idioma guardado
        applyApplicationLocale()
    }

    override fun attachBaseContext(base: Context) {
        // Aplicar el idioma guardado al contexto base
        val languageCode = getStoredLanguage(base)
        val context = LocaleHelper.setLocale(base, languageCode)
        super.attachBaseContext(context)
    }

    /**
     * Aplica el idioma guardado usando AppCompatDelegate (compatible con Android moderno)
     */
    private fun applyApplicationLocale() {
        val languageCode = LocaleHelper.getLanguage(this)
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    /**
     * Obtiene el idioma almacenado sin necesitar el helper completo
     */
    private fun getStoredLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return prefs.getString("selected_language", "es") ?: "es"
    }
}
