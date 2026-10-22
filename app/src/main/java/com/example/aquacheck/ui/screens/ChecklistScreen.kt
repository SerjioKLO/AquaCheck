package com.example.aquacheck.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aquacheck.model.EstadoItem
import com.example.aquacheck.model.ItemChecklist
import com.example.aquacheck.model.MockData
import com.example.aquacheck.ui.components.AppCard
import com.example.aquacheck.ui.components.AppTopAppBar
import com.example.aquacheck.ui.components.PrimaryButton
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

@Composable
fun ChecklistScreen(
    viewModel: PreChequeoViewModel,
    checklistId: String,
    onBackClick: () -> Unit,
    onNavigateToEvidencias: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChecklistContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onItemEstadoChange = { itemId, estado ->
            viewModel.updateItemEstado(itemId, estado)
        },
        onObservacionesChange = {
            viewModel.setObservacionesText(it)
        },
        onSiguienteClick = {
            viewModel.calcularResultado()
            onNavigateToEvidencias()
        },
        modifier = modifier
    )
}

@Composable
fun ChecklistContent(
    uiState: PreChequeoUiState,
    onBackClick: () -> Unit,
    onItemEstadoChange: (String, EstadoItem) -> Unit,
    onObservacionesChange: (String) -> Unit,
    onSiguienteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopAppBar(
                title = "Checklist de Pre-Chequeo",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
        ) {
            // Header Info Card
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Buzo: ${uiState.currentBuzo?.nombre ?: "Sin asignar"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
                Text(
                    text = "Centro: ${uiState.currentCentro?.nombre ?: "Sin asignar"} | Fecha: ${uiState.currentDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OnBackgroundText
                )
            }

            // LazyColumn of items + Observaciones field
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.checklistItems, key = { it.id }) { item ->
                    ChecklistItemCard(
                        item = item,
                        onEstadoSelected = { nuevoEstado ->
                            onItemEstadoChange(item.id, nuevoEstado)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    AppCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Observaciones generales",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = uiState.observacionesText,
                            onValueChange = onObservacionesChange,
                            placeholder = { Text("Ingrese observaciones (obligatorio si hay ítems 'No cumple' u 'Observado')", color = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = PrimaryDarkGreen,
                                unfocusedBorderColor = PrimaryDarkGreen,
                                focusedLabelColor = PrimaryDarkGreen,
                                unfocusedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }

            // Bottom Navigation Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                PrimaryButton(
                    text = "Siguiente a Evidencias",
                    icon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = onSiguienteClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ChecklistItemCard(
    item: ItemChecklist,
    onEstadoSelected: (EstadoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnBackgroundText,
                modifier = Modifier.weight(1f)
            )
            if (item.esCritico) {
                Text(
                    text = "(Crítico)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = ObservadoRed
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option 1: Cumple
            val isCumple = item.estado == EstadoItem.CUMPLE
            FilterChip(
                selected = isCumple,
                onClick = { onEstadoSelected(EstadoItem.CUMPLE) },
                label = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cumple",
                            fontSize = 10.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            fontWeight = if (isCumple) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isCumple) OnAptoContainerGreen else OnBackgroundText
                        )
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AptoContainerGreen,
                    selectedLabelColor = OnAptoContainerGreen
                ),
                modifier = Modifier.weight(1f)
            )

            // Option 2: No Cumple
            val isNoCumple = item.estado == EstadoItem.NO_CUMPLE
            FilterChip(
                selected = isNoCumple,
                onClick = { onEstadoSelected(EstadoItem.NO_CUMPLE) },
                label = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Cumple",
                            fontSize = 10.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            fontWeight = if (isNoCumple) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isNoCumple) ObservadoRed else OnBackgroundText
                        )
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ObservadoContainerRed,
                    selectedLabelColor = ObservadoRed
                ),
                modifier = Modifier.weight(1f)
            )

            // Option 3: No Aplica
            val isNoAplica = item.estado == EstadoItem.NO_APLICA
            FilterChip(
                selected = isNoAplica,
                onClick = { onEstadoSelected(EstadoItem.NO_APLICA) },
                label = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Aplica",
                            fontSize = 10.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            fontWeight = if (isNoAplica) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isNoAplica) Color.White else OnBackgroundText
                        )
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color.Gray,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            )

            // Option 4: Observado
            val isObservado = item.estado == EstadoItem.OBSERVADO
            FilterChip(
                selected = isObservado,
                onClick = { onEstadoSelected(EstadoItem.OBSERVADO) },
                label = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Observado",
                            fontSize = 10.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            fontWeight = if (isObservado) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isObservado) Color(0xFFE65100) else OnBackgroundText
                        )
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFFF3E0),
                    selectedLabelColor = Color(0xFFE65100)
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistScreenPreview() {
    AquaCheckTheme {
        ChecklistContent(
            uiState = PreChequeoUiState(
                currentBuzo = MockData.mockBuzos[0],
                currentCentro = MockData.mockCentros[0],
                currentDate = "2026-10-05",
                checklistItems = MockData.defaultChecklistItems
            ),
            onBackClick = {},
            onItemEstadoChange = { _, _ -> },
            onObservacionesChange = {},
            onSiguienteClick = {}
        )
    }
}
