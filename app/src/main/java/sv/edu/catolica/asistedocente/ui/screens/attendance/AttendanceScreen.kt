package sv.edu.catolica.asistedocente.ui.screens.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import sv.edu.catolica.asistedocente.ui.components.EstudianteAttendanceCard
import sv.edu.catolica.asistedocente.utils.DateUtils

/**
 * Pantalla de toma de asistencia (CRÍTICA)
 * Debe ser extremadamente rápida y fácil de usar
 * Optimizada para uso en el aula durante la clase
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onNavigateBack: () -> Unit,
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val attendanceStates by viewModel.attendanceStates.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = when (val state = uiState) {
                                is AttendanceUiState.Success -> state.groupName
                                is AttendanceUiState.Empty -> state.groupName
                                else -> "Asistencia"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = DateUtils.formatDate(selectedDate),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón de cambiar fecha
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Cambiar fecha")
                    }

                    // Menú de opciones
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Marcar todos presentes") },
                            onClick = {
                                viewModel.markAllAsPresent()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.DoneAll, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Limpiar todo") },
                            onClick = {
                                viewModel.clearAll()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            // Barra inferior con resumen y botón de guardar
            if (uiState is AttendanceUiState.Success) {
                AttendanceBottomBar(
                    count = viewModel.getAttendanceCount(),
                    isSaving = isSaving,
                    onSaveClick = { showSaveDialog = true }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is AttendanceUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is AttendanceUiState.Empty -> {
                    EmptyStudentsState(
                        groupName = state.groupName,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is AttendanceUiState.Success -> {
                    AttendanceList(
                        students = state.students,
                        attendanceStates = attendanceStates,
                        onStateChange = { studentId, estado ->
                            viewModel.updateAttendanceState(studentId, estado)
                        }
                    )
                }

                is AttendanceUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = onNavigateBack,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    // Diálogo de confirmación de guardado
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Guardar asistencia") },
            text = {
                val count = viewModel.getAttendanceCount()
                Text(
                    "¿Desea guardar la asistencia?\n\n" +
                            "Presentes: ${count.presentes}\n" +
                            "Ausentes: ${count.ausentes}\n" +
                            "Tardanzas: ${count.tardanzas}\n" +
                            "Justificados: ${count.justificados}\n" +
                            "Sin marcar: ${count.sinMarcar}"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSaveDialog = false
                        viewModel.saveAttendance(
                            onSuccess = {
                                showSuccessDialog = true
                            },
                            onError = { error ->
                                errorMessage = error
                            }
                        )
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Éxito!") },
            text = { Text("La asistencia se ha guardado correctamente.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Aceptar")
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }

    // Mostrar error si existe
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Error") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                Button(onClick = { errorMessage = null }) {
                    Text("Aceptar")
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }
}

/**
 * Lista de estudiantes con botones de asistencia
 */
@Composable
private fun AttendanceList(
    students: List<sv.edu.catolica.asistedocente.data.local.entities.Estudiante>,
    attendanceStates: Map<Long, sv.edu.catolica.asistedocente.data.local.entities.EstadoAsistencia>,
    onStateChange: (Long, sv.edu.catolica.asistedocente.data.local.entities.EstadoAsistencia) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(students, key = { it.id }) { student ->
            EstudianteAttendanceCard(
                estudiante = student,
                currentState = attendanceStates[student.id],
                onStateChange = { estado ->
                    onStateChange(student.id, estado)
                }
            )
        }
    }
}

/**
 * Barra inferior con resumen y botón de guardar
 */
@Composable
private fun AttendanceBottomBar(
    count: AttendanceCount,
    isSaving: Boolean,
    onSaveClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Resumen rápido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttendanceSummaryItem("P", count.presentes.toString(), androidx.compose.ui.graphics.Color(0xFF4CAF50))
                AttendanceSummaryItem("A", count.ausentes.toString(), androidx.compose.ui.graphics.Color(0xFFF44336))
                AttendanceSummaryItem("T", count.tardanzas.toString(), androidx.compose.ui.graphics.Color(0xFFFF9800))
                AttendanceSummaryItem("J", count.justificados.toString(), androidx.compose.ui.graphics.Color(0xFF2196F3))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón de guardar
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Asistencia")
                }
            }
        }
    }
}

/**
 * Item individual del resumen
 */
@Composable
private fun AttendanceSummaryItem(
    label: String,
    count: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Estado vacío cuando no hay estudiantes
 */
@Composable
private fun EmptyStudentsState(
    groupName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.PersonOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay estudiantes",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Este grupo no tiene estudiantes registrados.\nAgrega estudiantes para poder tomar asistencia.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Estado de error
 */
@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text("Volver")
        }
    }
}
