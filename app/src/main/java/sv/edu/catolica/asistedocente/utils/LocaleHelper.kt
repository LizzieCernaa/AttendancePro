package sv.edu.catolica.asistedocente.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

/**
 * Helper para manejar el cambio de idioma en la aplicación
 * Soporta español, inglés y portugués
 */
object LocaleHelper {

    private const val SELECTED_LANGUAGE = "selected_language"
    private const val DEFAULT_LANGUAGE = "es" // Español por defecto

    /**
     * Idiomas soportados
     */
    enum class Language(val code: String) {
        SPANISH("es"),
        ENGLISH("en"),
        PORTUGUESE("pt")
    }

    /**
     * Aplica el idioma seleccionado al context
     */
    fun setLocale(context: Context, languageCode: String): Context {
        persist(context, languageCode)
        return updateResources(context, languageCode)
    }

    /**
     * Obtiene el idioma actualmente configurado
     */
    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    /**
     * Verifica si el idioma dado está soportado
     */
    fun isSupportedLanguage(languageCode: String): Boolean {
        return Language.values().any { it.code == languageCode }
    }

    /**
     * Obtiene todos los idiomas soportados
     */
    fun getSupportedLanguages(): List<Language> {
        return Language.values().toList()
    }

    /**
     * Guarda el idioma seleccionado en SharedPreferences
     */
    private fun persist(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        prefs.edit().putString(SELECTED_LANGUAGE, languageCode).apply()
    }

    /**
     * Actualiza los recursos del contexto con el nuevo idioma
     */
    private fun updateResources(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }

    /**
     * Obtiene el nombre localizado del idioma
     */
    fun getLanguageName(context: Context, language: Language): String {
        return when (language) {
            Language.SPANISH -> "Español"
            Language.ENGLISH -> "English"
            Language.PORTUGUESE -> "Português"
        }
    }
}
