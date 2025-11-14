package sv.edu.catolica.asistedocente.ui.screens.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import sv.edu.catolica.asistedocente.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de reportes de asistencia
 * Permite seleccionar grupo y rango de fechas, ver estadísticas y exportar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val grupos by viewModel.grupos.collectAsState()
    val selectedGroupId by viewModel.selectedGroupId.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val reportData by viewModel.reportData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showGroupDropdown by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showExportMenu by remember { mutableStateOf(false) }
    var pdfFilePath by remember { mutableStateOf<String?>(null) }
    var showShareDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes de Asistencia") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón de exportación
                    IconButton(
                        onClick = { showExportMenu = true },
                        enabled = reportData != null && !isLoading
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Exportar")
                    }
                    DropdownMenu(
                        expanded = showExportMenu,
                        onDismissRequest = { showExportMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Exportar a PDF") },
                            onClick = {
                                viewModel.exportToPdf(
                                    onSuccess = { filePath ->
                                        pdfFilePath = filePath
                                        showShareDialog = true
                                        scope.launch {
                                            snackbarHostState.showSnackbar("PDF generado exitosamente")
                                        }
                                    },
                                    onError = { error ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Error: $error")
                                        }
                                    }
                                )
                                showExportMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Exportar a Excel") },
                            onClick = {
                                viewModel.exportToExcel(
                                    onSuccess = { },
                                    onError = { error ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Error: $error")
                                        }
                                    }
                                )
                                showExportMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.TableChart, contentDescription = null)
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Filtros
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Filtros",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Selector de grupo
                    ExposedDropdownMenuBox(
                        expanded = showGroupDropdown,
                        onExpandedChange = { showGroupDropdown = !showGroupDropdown }
                    ) {
                        OutlinedTextField(
                            value = grupos.find { it.id == selectedGroupId }?.nombre ?: "Seleccionar grupo",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Grupo") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGroupDropdown)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = showGroupDropdown,
                            onDismissRequest = { showGroupDropdown = false }
                        ) {
                            grupos.forEach { grupo ->
                                DropdownMenuItem(
                                    text = { Text(grupo.nombre) },
                                    onClick = {
                                        viewModel.selectGroup(grupo.id)
                                        showGroupDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Selector de fecha de inicio
                    OutlinedTextField(
                        value = DateUtils.formatDate(startDate),
                        onValueChange = {},
                        label = { Text("Fecha de inicio") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showStartDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Selector de fecha de fin
                    OutlinedTextField(
                        value = DateUtils.formatDate(endDate),
                        onValueChange = {},
                        label = { Text("Fecha de fin") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showEndDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Botón de generar reporte
                    Button(
                        onClick = { viewModel.generateReport() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedGroupId != null && !isLoading
                    ) {
                        Icon(Icons.Default.Assessment, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generar Reporte")
                    }
                }
            }

            // Contenido del reporte
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (reportData != null) {
                ReportContent(reportData = reportData!!)
            } else if (selectedGroupId != null) {
                // Estado vacío
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Genera un reporte para ver las estadísticas",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // DatePickers (implementación simplificada - en producción usar DatePickerDialog)
    // TODO: Implementar DatePicker dialogs nativos

    // Diálogo para compartir/abrir PDF
    if (showShareDialog && pdfFilePath != null) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text("PDF Generado") },
            text = { Text("El reporte PDF ha sido generado exitosamente. ¿Qué deseas hacer?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.sharePdf(pdfFilePath!!)
                        showShareDialog = false
                    }
                ) {
                    Text("Compartir")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            viewModel.openPdf(pdfFilePath!!)
                            showShareDialog = false
                        }
                    ) {
                        Text("Abrir")
                    }
                    TextButton(
                        onClick = { showShareDialog = false }
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        )
    }
}

/**
 * Contenido del reporte con estadísticas
 */
@Composable
private fun ReportContent(reportData: ReportData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Encabezado
        Text(
            text = "Estadísticas de Asistencia",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // Información general
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoRow("Total de estudiantes:", "${reportData.totalEstudiantes}")
                InfoRow("Días registrados:", "${reportData.diasRegistrados}")
                InfoRow("Total de registros:", "${reportData.totalRegistros}")
            }
        }

        // Estadísticas por estado
        Text(
            text = "Distribución de Asistencia",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Presentes
        StatCard(
            title = "Presentes",
            count = reportData.presentes,
            percentage = reportData.porcentajePresentes,
            color = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.CheckCircle
        )

        // Ausentes
        StatCard(
            title = "Ausentes",
            count = reportData.ausentes,
            percentage = reportData.porcentajeAusentes,
            color = MaterialTheme.colorScheme.error,
            icon = Icons.Default.Cancel
        )

        // Tardanzas
        StatCard(
            title = "Tardanzas",
            count = reportData.tardanzas,
            percentage = reportData.porcentajeTardanzas,
            color = MaterialTheme.colorScheme.tertiary,
            icon = Icons.Default.Schedule
        )

        // Justificados
        StatCard(
            title = "Justificados",
            count = reportData.justificados,
            percentage = reportData.porcentajeJustificados,
            color = MaterialTheme.colorScheme.secondary,
            icon = Icons.Default.EventNote
        )

        // Porcentaje general de asistencia
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Porcentaje de Asistencia General",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = String.format("%.1f%%", reportData.porcentajeAsistenciaGeneral),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

/**
 * Fila de información
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Card de estadística con icono
 */
@Composable
private fun StatCard(
    title: String,
    count: Int,
    percentage: Float,
    color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$count registros",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = String.format("%.1f%%", percentage),
                style = MaterialTheme.typography.headlineMedium,
                color = color
            )
        }
    }
}
