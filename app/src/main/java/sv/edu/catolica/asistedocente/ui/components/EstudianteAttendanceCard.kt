package sv.edu.catolica.asistedocente.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import sv.edu.catolica.asistedocente.data.local.entities.Estudiante
import sv.edu.catolica.asistedocente.data.local.entities.EstadoAsistencia

/**
 * Componente Card para mostrar estudiante con botones de asistencia
 * Optimizado para rapidez en la toma de asistencia
 */
@Composable
fun EstudianteAttendanceCard(
    estudiante: Estudiante,
    currentState: EstadoAsistencia?,
    onStateChange: (EstadoAsistencia) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (currentState) {
                EstadoAsistencia.PRESENTE -> Color(0xFFE8F5E9)  // Verde claro
                EstadoAsistencia.AUSENTE -> Color(0xFFFFEBEE)   // Rojo claro
                EstadoAsistencia.TARDANZA -> Color(0xFFFFF3E0)  // Naranja claro
                EstadoAsistencia.JUSTIFICADO -> Color(0xFFE3F2FD) // Azul claro
                null -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Información del estudiante
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${estudiante.nombre} ${estudiante.apellido}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Código: ${estudiante.codigo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Indicador de estado actual
                if (currentState != null) {
                    Icon(
                        imageVector = when (currentState) {
                            EstadoAsistencia.PRESENTE -> Icons.Default.CheckCircle
                            EstadoAsistencia.AUSENTE -> Icons.Default.Cancel
                            EstadoAsistencia.TARDANZA -> Icons.Default.AccessTime
                            EstadoAsistencia.JUSTIFICADO -> Icons.Default.Description
                        },
                        contentDescription = null,
                        tint = when (currentState) {
                            EstadoAsistencia.PRESENTE -> Color(0xFF4CAF50)
                            EstadoAsistencia.AUSENTE -> Color(0xFFF44336)
                            EstadoAsistencia.TARDANZA -> Color(0xFFFF9800)
                            EstadoAsistencia.JUSTIFICADO -> Color(0xFF2196F3)
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botones de estado (diseño optimizado para toques rápidos)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Botón: Presente
                AttendanceButton(
                    text = "P",
                    icon = Icons.Default.Check,
                    color = Color(0xFF4CAF50),
                    isSelected = currentState == EstadoAsistencia.PRESENTE,
                    onClick = { onStateChange(EstadoAsistencia.PRESENTE) },
                    modifier = Modifier.weight(1f)
                )

                // Botón: Ausente
                AttendanceButton(
                    text = "A",
                    icon = Icons.Default.Close,
                    color = Color(0xFFF44336),
                    isSelected = currentState == EstadoAsistencia.AUSENTE,
                    onClick = { onStateChange(EstadoAsistencia.AUSENTE) },
                    modifier = Modifier.weight(1f)
                )

                // Botón: Tardanza
                AttendanceButton(
                    text = "T",
                    icon = Icons.Default.Schedule,
                    color = Color(0xFFFF9800),
                    isSelected = currentState == EstadoAsistencia.TARDANZA,
                    onClick = { onStateChange(EstadoAsistencia.TARDANZA) },
                    modifier = Modifier.weight(1f)
                )

                // Botón: Justificado
                AttendanceButton(
                    text = "J",
                    icon = Icons.Default.Description,
                    color = Color(0xFF2196F3),
                    isSelected = currentState == EstadoAsistencia.JUSTIFICADO,
                    onClick = { onStateChange(EstadoAsistencia.JUSTIFICADO) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Botón individual de asistencia
 */
@Composable
private fun AttendanceButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
