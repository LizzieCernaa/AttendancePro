package sv.edu.catolica.asistedocente.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Helper para gestionar la autenticación del docente
 */
object AuthHelper {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_DOCENTE_ID = "docente_id"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_DOCENTE_EMAIL = "docente_email"
    private const val KEY_DOCENTE_NOMBRE = "docente_nombre"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Guarda la sesión del docente
     */
    fun saveSession(
        context: Context,
        docenteId: Long,
        email: String,
        nombre: String
    ) {
        getPrefs(context).edit().apply {
            putLong(KEY_DOCENTE_ID, docenteId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_DOCENTE_EMAIL, email)
            putString(KEY_DOCENTE_NOMBRE, nombre)
            apply()
        }
    }

    /**
     * Verifica si hay una sesión activa
     */
    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Obtiene el ID del docente logueado
     */
    fun getDocenteId(context: Context): Long? {
        if (!isLoggedIn(context)) return null
        val id = getPrefs(context).getLong(KEY_DOCENTE_ID, -1L)
        return if (id != -1L) id else null
    }

    /**
     * Obtiene el email del docente logueado
     */
    fun getDocenteEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_DOCENTE_EMAIL, null)
    }

    /**
     * Obtiene el nombre del docente logueado
     */
    fun getDocenteNombre(context: Context): String? {
        return getPrefs(context).getString(KEY_DOCENTE_NOMBRE, null)
    }

    /**
     * Cierra la sesión del docente
     */
    fun logout(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
