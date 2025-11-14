package sv.edu.catolica.asistedocente

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import sv.edu.catolica.asistedocente.ui.navigation.NavGraph
import sv.edu.catolica.asistedocente.ui.theme.AsisteDocenteTheme
import sv.edu.catolica.asistedocente.utils.LocaleHelper

/**
 * MainActivity principal de la aplicación
 * Anotada con @AndroidEntryPoint para habilitar la inyección de dependencias de Hilt
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Aplicar el idioma guardado al contexto
        val languageCode = LocaleHelper.getLanguage(newBase)
        val context = LocaleHelper.setLocale(newBase, languageCode)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AsisteDocenteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}