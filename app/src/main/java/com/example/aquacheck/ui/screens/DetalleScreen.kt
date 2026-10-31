package com.example.aquacheck.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.aquacheck.model.EstadoItem
import com.example.aquacheck.model.EvidenciaItem
import com.example.aquacheck.model.MockData
import com.example.aquacheck.model.PreChequeo
import com.example.aquacheck.ui.components.AppCard
import com.example.aquacheck.ui.components.AppTopAppBar
import com.example.aquacheck.ui.components.StatusChip
import com.example.aquacheck.ui.theme.AptoContainerGreen
import com.example.aquacheck.ui.theme.AquaCheckTheme
import com.example.aquacheck.ui.theme.BackgroundLight
import com.example.aquacheck.ui.theme.OnAptoContainerGreen
import com.example.aquacheck.ui.theme.ObservadoContainerRed
import com.example.aquacheck.ui.theme.ObservadoRed
import com.example.aquacheck.ui.theme.OutlineBorder
import com.example.aquacheck.ui.theme.PrimaryDarkGreen
import com.example.aquacheck.viewmodel.DetalleViewModel
import java.io.File

@Composable
fun DetalleScreen(
    viewModel: DetalleViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chequeo by viewModel.chequeo.collectAsStateWithLifecycle()

    DetalleContent(
        chequeo = chequeo,
        onBackClick = onBackClick,
        onDeleteClick = {
            viewModel.eliminarChequeo {
                onBackClick()
            }
        },
        modifier = modifier
    )
}

@Composable
fun DetalleContent(
    chequeo: PreChequeo?,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val resultado = chequeo?.resultado ?: "Apto"
    val isApto = resultado.equals("Apto", ignoreCase = true)
    val headerContainerColor = if (isApto) AptoContainerGreen else ObservadoContainerRed
    val headerTextColor = if (isApto) OnAptoContainerGreen else ObservadoRed
    val headerIcon = if (isApto) Icons.Rounded.CheckCircle else Icons.Rounded.Warning
    val statusText = if (isApto) "ESTADO: APTO" else "ESTADO: ${resultado.uppercase()}"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopAppBar(
                title = "Detalle de Evaluación",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        if (chequeo == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(BackgroundLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cargando detalle de evaluación...",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(BackgroundLight)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Encabezado de Evaluación
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerContainerColor, shape = RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = headerIcon,
                            contentDescription = statusText,
                            tint = headerTextColor,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = headerTextColor
                        )
                        StatusChip(status = chequeo.resultado)
                    }
                }

                // Datos Generales
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Datos Generales",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailRow(label = "Centro de Operación", value = chequeo.centro.nombre)
                    DetailRow(label = "Ubicación", value = chequeo.centro.ubicacion)
                    DetailRow(label = "Buzo", value = "${chequeo.buzo.nombre} (${chequeo.buzo.certificacion})")
                    DetailRow(label = "Supervisor a cargo", value = chequeo.supervisor.nombre)
                    DetailRow(label = "Centro de Operación", value = "${chequeo.centro.nombre} - ${chequeo.centro.ubicacion}")
                    DetailRow(label = "Fecha y Hora", value = chequeo.fecha)
                }

                // Checklist
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Checklist y Hallazgos Críticos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val fallasItems = chequeo.items.filter {
                        it.estado == EstadoItem.NO_CUMPLE || it.estado == EstadoItem.OBSERVADO
                    }

                    if (fallasItems.isEmpty()) {
                        Text(
                            text = "Todos los ítems del checklist cumplen satisfactoriamente.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    } else {
                        Text(
                            text = "Ítems con observaciones o no cumple:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = ObservadoRed
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        fallasItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• ${item.nombre}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                StatusChip(
                                    status = if (item.estado == EstadoItem.NO_CUMPLE) "No Cumple" else "Observado"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = OutlineBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Observaciones del Supervisor:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Text(
                        text = if (chequeo.observaciones.isNotBlank()) chequeo.observaciones else "Sin observaciones registradas.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                // Sección 2: Parámetros Médicos de Salud
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Parámetros Médicos de Salud",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailRow(label = "Presión Sistólica", value = "${chequeo.sistolica} mmHg")
                    DetailRow(label = "Presión Diastólica", value = "${chequeo.diastolica} mmHg")
                    DetailRow(label = "Saturación O₂", value = "${chequeo.saturacion}%")
                    DetailRow(label = "Frecuencia Cardíaca", value = "${chequeo.frecuenciaCardiaca} lpm")
                    DetailRow(label = "Horas de Sueño", value = "${chequeo.horasSueno} hrs")
                    DetailRow(label = "Último Consumo de Alcohol", value = "${chequeo.horasUltimoAlcohol} hrs atrás")
                    DetailRow(label = "Durmió menos de 6 horas", value = if (chequeo.horasSueno < 6) "Sí (Alerta)" else "No")
                    DetailRow(label = "Resfrío o dolor de oídos", value = if (chequeo.tieneCongestionNasal) "Sí (Alerta)" else "No")
                    DetailRow(label = "Consumió alcohol en las últimas 24h", value = if (chequeo.horasUltimoAlcohol < 24 && chequeo.horasUltimoAlcohol > 0) "Sí (Alerta)" else "No")
                }

                // Sección 3: Evidencias Fotográficas
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Evidencias Fotográficas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = "${chequeo.evidencias.size} fotos",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (chequeo.evidencias.isEmpty()) {
                        Text(
                            text = "No se adjuntaron evidencias fotográficas en esta evaluación.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(chequeo.evidencias, key = { it.id }) { evidencia ->
                                DetalleEvidenciaItem(evidencia = evidencia)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = "Eliminar registro de evaluación",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Pre-Chequeo", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("¿Está seguro de que desea eliminar este registro de pre-chequeo? Esta acción no se puede deshacer.", color = MaterialTheme.colorScheme.onSurface) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = PrimaryDarkGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = PrimaryDarkGreen
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
private fun DetalleEvidenciaItem(
    evidencia: EvidenciaItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(110.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Color(0xFFE2E8F0))
            ) {
                if (evidencia.bitmap != null) {
                    Image(
                        bitmap = evidencia.bitmap.asImageBitmap(),
                        contentDescription = evidencia.titulo,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (evidencia.imagePath != null) {
                    AsyncImage(
                        model = File(evidencia.imagePath),
                        contentDescription = evidencia.titulo,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (evidencia.drawableRes != null) {
                    Image(
                        painter = painterResource(id = evidencia.drawableRes),
                        contentDescription = evidencia.titulo,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Text(
                text = evidencia.titulo,
                modifier = Modifier.padding(6.dp),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetalleScreenPreview() {
    AquaCheckTheme {
        DetalleContent(
            chequeo = MockData.mockPreChequeos[1],
            onBackClick = {},
            onDeleteClick = {}
        )
    }
}
