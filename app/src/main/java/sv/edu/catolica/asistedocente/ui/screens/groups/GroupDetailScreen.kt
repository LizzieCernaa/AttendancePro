package sv.edu.catolica.asistedocente.ui.screens.groups

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
import sv.edu.catolica.asistedocente.ui.components.EstudianteCard

/**
 * Pantalla de detalle de grupo
 * Muestra información del grupo y lista de estudiantes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditGroup: (Long) -> Unit,
    onNavigateToAddStudent: (Long) -> Unit,
    onNavigateToEditStudent: (Long, Long) -> Unit,
    onNavigateToAttendance: (Long) -> Unit,
    viewModel: GroupDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var studentToDelete by remember { mutableStateOf<Long?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (val state = uiState) {
                            is GroupDetailUiState.Success -> state.grupo.nombre
                            else -> "Detalle de Grupo"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar grupo") },
                            onClick = {
                                showMenu = false
                                if (uiState is GroupDetailUiState.Success) {
                                    onNavigateToEditGroup((uiState as GroupDetailUiState.Success).grupo.id)
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar grupo") },
                            onClick = {
                                showMenu = false
                                viewModel.showDeleteConfirmation()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Actualizar") },
                            onClick = {
                                showMenu = false
                                viewModel.refresh()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // FAB para agregar estudiante
            if (uiState is GroupDetailUiState.Success) {
                FloatingActionButton(
                    onClick = {
                        val groupId = (uiState as GroupDetailUiState.Success).grupo.id
                        onNavigateToAddStudent(groupId)
                    }
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Agregar estudiante")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is GroupDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is GroupDetailUiState.Success -> {
                    GroupDetailContent(
                        grupo = state.grupo,
                        estudiantes = state.estudiantes,
                        onTakeAttendance = { onNavigateToAttendance(state.grupo.id) },
                        onAddStudent = { onNavigateToAddStudent(state.grupo.id) },
                        onEditStudent = { studentId ->
                            onNavigateToEditStudent(state.grupo.id, studentId)
                        },
                        onDeleteStudent = { studentId ->
                            studentToDelete = studentId
                        }
                    )
                }

                is GroupDetailUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.refresh() },
                        onBack = onNavigateBack,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    // Diálogo de confirmación de eliminación de grupo
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteDialog() },
            title = { Text("Eliminar grupo") },
            text = { Text("¿Está seguro de que desea eliminar este grupo? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteGroup(
                            onSuccess = { onNavigateBack() },
                            onError = { error -> errorMessage = error }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteDialog() }) {
                    Text("Cancelar")
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }

    // Diálogo de confirmación de eliminación de estudiante
    if (studentToDelete != null) {
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text("Eliminar estudiante") },
            text = { Text("¿Está seguro de que desea eliminar este estudiante del grupo?") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = studentToDelete!!
                        viewModel.deleteEstudiante(
                            id,
                            onSuccess = {
                                studentToDelete = null
                                successMessage = "Estudiante eliminado correctamente"
                            },
                            onError = { error ->
                                studentToDelete = null
                                errorMessage = error
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { studentToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Snackbar de error
    if (errorMessage != null) {
        LaunchedEffect(errorMessage) {
            kotlinx.coroutines.delay(3000)
            errorMessage = null
        }
    }

    // Snackbar de éxito
    if (successMessage != null) {
        LaunchedEffect(successMessage) {
            kotlinx.coroutines.delay(2000)
            successMessage = null
        }
    }
}

/**
 * Contenido principal de la pantalla
 */
@Composable
private fun GroupDetailContent(
    grupo: sv.edu.catolica.asistedocente.data.local.entities.Grupo,
    estudiantes: List<sv.edu.catolica.asistedocente.data.local.entities.Estudiante>,
    onTakeAttendance: () -> Unit,
    onAddStudent: () -> Unit,
    onEditStudent: (Long) -> Unit,
    onDeleteStudent: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Información del grupo
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = grupo.nombre,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = grupo.materia,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    if (grupo.horario != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = grupo.horario,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    if (grupo.descripcion != null && grupo.descripcion.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = grupo.descripcion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // Botón de tomar asistencia (destacado)
        item {
            Button(
                onClick = onTakeAttendance,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tomar Asistencia Hoy", style = MaterialTheme.typography.titleMedium)
            }
        }

        // Estadísticas
        item {
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem(
                        icon = Icons.Default.People,
                        label = "Estudiantes",
                        value = estudiantes.size.toString()
                    )
                    StatItem(
                        icon = Icons.Default.CheckCircle,
                        label = "Activos",
                        value = estudiantes.count { it.activo }.toString()
                    )
                }
            }
        }

        // Lista de estudiantes
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estudiantes (${estudiantes.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onAddStudent) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agregar")
                }
            }
        }

        if (estudiantes.isEmpty()) {
            item {
                EmptyStudentsState(onAddStudent = onAddStudent)
            }
        } else {
            items(estudiantes, key = { it.id }) { estudiante ->
                EstudianteCard(
                    estudiante = estudiante,
                    onCardClick = { /* Ver detalle del estudiante */ },
                    onEditClick = { onEditStudent(estudiante.id) },
                    onDeleteClick = { onDeleteStudent(estudiante.id) }
                )
            }
        }
    }
}

/**
 * Item de estadística
 */
@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
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
    onAddStudent: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.PersonOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay estudiantes",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Agrega estudiantes para comenzar a registrar asistencia",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAddStudent) {
                Icon(Icons.Default.PersonAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar Estudiante")
            }
        }
    }
}

/**
 * Estado de error
 */
@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onBack) {
                Text("Volver")
            }
            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reintentar")
            }
        }
    }
}
