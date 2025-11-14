package sv.edu.catolica.asistedocente.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import sv.edu.catolica.asistedocente.R
import sv.edu.catolica.asistedocente.utils.LocaleHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Sección de Idioma
            Text(
                text = stringResource(R.string.settings_language_section),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
            )

            // Item de idioma
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_language)) },
                supportingContent = { Text(getLanguageName(uiState.currentLanguage)) },
                leadingContent = {
                    Icon(
                        Icons.Default.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.clickable { showLanguageDialog = true }
            )

            Divider()

            // Información de la aplicación
            Text(
                text = stringResource(R.string.settings_about_section),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.app_name)) },
                supportingContent = { Text(stringResource(R.string.settings_version, "1.0.0")) }
            )
        }
    }

    // Diálogo de selección de idioma
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.settings_select_language)) },
            text = {
                Column {
                    LocaleHelper.Language.values().forEach { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.changeLanguage(language.code)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.currentLanguage == language.code,
                                onClick = {
                                    viewModel.changeLanguage(language.code)
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = getLanguageNativeName(language.code),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (uiState.currentLanguage == language.code) {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Seleccionado",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Mostrar diálogo de reinicio si es necesario
    if (uiState.showRestartDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.settings_restart_required)) },
            text = { Text(stringResource(R.string.settings_restart_message)) },
            confirmButton = {
                TextButton(onClick = { viewModel.restartApp() }) {
                    Text(stringResource(R.string.restart))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissRestartDialog() }) {
                    Text(stringResource(R.string.later))
                }
            }
        )
    }
}

/**
 * Obtiene el nombre del idioma en el idioma actual
 */
@Composable
private fun getLanguageName(code: String): String {
    return when (code) {
        "es" -> stringResource(R.string.language_spanish)
        "en" -> stringResource(R.string.language_english)
        "pt" -> stringResource(R.string.language_portuguese)
        else -> stringResource(R.string.language_spanish)
    }
}

/**
 * Obtiene el nombre del idioma en su idioma nativo
 */
private fun getLanguageNativeName(code: String): String {
    return when (code) {
        "es" -> "Español"
        "en" -> "English"
        "pt" -> "Português"
        else -> "Español"
    }
}
