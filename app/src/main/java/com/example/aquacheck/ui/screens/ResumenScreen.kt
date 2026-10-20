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
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.aquacheck.model.EstadoItem
import com.example.aquacheck.model.EvidenciaItem
import com.example.aquacheck.model.MockData
import com.example.aquacheck.model.Resultado
import com.example.aquacheck.ui.components.AppCard
import com.example.aquacheck.ui.components.AppTopAppBar
import com.example.aquacheck.ui.components.PrimaryButton
import com.example.aquacheck.ui.components.StatusChip
import com.example.aquacheck.ui.theme.AptoContainerGreen
import com.example.aquacheck.ui.theme.AquaCheckTheme
import com.example.aquacheck.ui.theme.BackgroundLight
import com.example.aquacheck.ui.theme.OnAptoContainerGreen
import com.example.aquacheck.ui.theme.OnBackgroundText
import com.example.aquacheck.ui.theme.ObservadoContainerRed
import com.example.aquacheck.ui.theme.ObservadoRed
import com.example.aquacheck.ui.theme.PrimaryDarkGreen
import com.example.aquacheck.viewmodel.PreChequeoUiState
import com.example.aquacheck.viewmodel.PreChequeoViewModel
import java.io.File

@Composable
fun ResumenScreen(
    viewModel: PreChequeoViewModel,
    onBackClick: () -> Unit,
    onSaveAndFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val resultado = uiState.resultado ?: viewModel.calcularResultado()

    ResumenContent(
        uiState = uiState,
        resultado = resultado,
        onBackClick = onBackClick,
        onFirmarYGuardar = {
            viewModel.guardarPreChequeo()
            onSaveAndFinish()
        },
        modifier = modifier
    )
}

@Composable
fun ResumenContent(
    uiState: PreChequeoUiState,
    resultado: Resultado,
    onBackClick: () -> Unit,
    onFirmarYGuardar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isApto = resultado.estado.equals("Apto", ignoreCase = true)
    val headerContainerColor = if (isApto) AptoContainerGreen else ObservadoContainerRed
    val headerTextColor = if (isApto) OnAptoContainerGreen else ObservadoRed
    val headerIcon = if (isApto) Icons.Rounded.CheckCircle else Icons.Rounded.Warning
    val statusText = if (isApto) "ESTADO: APTO" else "ESTADO: OBSERVADO"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopAppBar(
                title = "Resumen del Pre-Chequeo",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Big Status Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerContainerColor, shape = RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = headerIcon,
                        contentDescription = statusText,
                        tint = headerTextColor,
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = headerTextColor
                    )
                    Text(
                        text = "Evaluación: ${resultado.resumenChecklist}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = headerTextColor
                    )
                }
            }

            // Summary Card
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Detalles de la Inspección",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                DetailItem(label = "Fecha", value = uiState.currentDate)
                DetailItem(
                    label = "Buzo",
                    value = "${uiState.currentBuzo?.nombre ?: "-"} (${uiState.currentBuzo?.certificacion ?: ""})"
                )
                DetailItem(
                    label = "Supervisor",
                    value = uiState.currentSupervisor?.nombre ?: "-"
                )
                DetailItem(
                    label = "Centro",
                    value = "${uiState.currentCentro?.nombre ?: "-"} - ${uiState.currentCentro?.ubicacion ?: ""}"
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Desglose de Puntos Evaluados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                uiState.checklistItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.nombre,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        val statusString = when (item.estado) {
                            EstadoItem.CUMPLE -> "APTO"
                            EstadoItem.OBSERVADO -> "OBSERVADO"
                            EstadoItem.NO_CUMPLE -> "OBSERVADO"
                            EstadoItem.PENDIENTE -> "PENDIENTE"
                            EstadoItem.NO_APLICA -> "N/A"
                        }
                        StatusChip(status = statusString)
                    }
                }
            }

            // Evidencias Card
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Evidencias Registradas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Text(
                        text = "${uiState.evidencias.size} fotos",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.evidencias.isEmpty()) {
                    Text(
                        text = "No se adjuntaron evidencias fotográficas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.evidencias, key = { it.id }) { evidencia ->
                            EvidenciaThumbItem(evidencia = evidencia)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = "Firmar y Guardar",
                icon = Icons.Rounded.Draw,
                onClick = onFirmarYGuardar,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun EvidenciaThumbItem(
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
                color = OnBackgroundText,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResumenScreenPreview() {
    AquaCheckTheme {
        ResumenContent(
            uiState = PreChequeoUiState(
                currentBuzo = MockData.mockBuzos[0],
                currentSupervisor = MockData.mockSupervisores[0],
                currentCentro = MockData.mockCentros[0],
                currentDate = "2026-10-05",
                checklistItems = MockData.defaultChecklistItems
            ),
            resultado = Resultado(
                estado = "Apto",
                fecha = "2026-10-05",
                resumenChecklist = "6/6 Cumple"
            ),
            onBackClick = {},
            onFirmarYGuardar = {}
        )
    }
}
