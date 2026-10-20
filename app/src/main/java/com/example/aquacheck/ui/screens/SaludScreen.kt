package com.example.aquacheck.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalBar
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aquacheck.model.MockData
import com.example.aquacheck.ui.components.AppCard
import com.example.aquacheck.ui.components.AppTopAppBar
import com.example.aquacheck.ui.components.PrimaryButton
import com.example.aquacheck.ui.theme.AptoContainerGreen
import com.example.aquacheck.ui.theme.AquaCheckTheme
import com.example.aquacheck.ui.theme.BackgroundLight
import com.example.aquacheck.ui.theme.OnAptoContainerGreen
import com.example.aquacheck.ui.theme.ObservadoContainerRed
import com.example.aquacheck.ui.theme.ObservadoRed
import com.example.aquacheck.ui.theme.PrimaryDarkGreen
import com.example.aquacheck.viewmodel.PreChequeoUiState
import com.example.aquacheck.viewmodel.PreChequeoViewModel

@Composable
fun SaludScreen(
    viewModel: PreChequeoViewModel,
    onBackClick: () -> Unit,
    onNavigateToChecklist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SaludContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onFrecuenciaCardiacaChange = { viewModel.setFrecuenciaCardiaca(it) },
        onSistolicaChange = { viewModel.setSistolica(it) },
        onDiastolicaChange = { viewModel.setDiastolica(it) },
        onSaturacionChange = { viewModel.setSaturacion(it) },
        onHorasSuenoChange = { viewModel.setHorasSueno(it) },
        onHorasUltimoAlcoholChange = { viewModel.setHorasUltimoAlcohol(it) },
        onCongestionNasalChange = { viewModel.setTieneCongestionNasal(it) },
        onMedicamentosNuevosChange = { viewModel.setTomaMedicamentosNuevos(it) },
        onEstadoPsicologicoCalmoChange = { viewModel.setEstadoPsicologicoCalmo(it) },
        onContinuarClick = {
            viewModel.validarSalud()
            onNavigateToChecklist()
        },
        modifier = modifier
    )
}

@Composable
fun SaludContent(
    uiState: PreChequeoUiState,
    onBackClick: () -> Unit,
    onFrecuenciaCardiacaChange: (String) -> Unit,
    onSistolicaChange: (String) -> Unit,
    onDiastolicaChange: (String) -> Unit,
    onSaturacionChange: (String) -> Unit,
    onHorasSuenoChange: (String) -> Unit,
    onHorasUltimoAlcoholChange: (String) -> Unit,
    onCongestionNasalChange: (Boolean) -> Unit,
    onMedicamentosNuevosChange: (Boolean) -> Unit,
    onEstadoPsicologicoCalmoChange: (Boolean) -> Unit,
    onContinuarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasBlankFields = uiState.frecuenciaCardiacaText.isBlank() ||
        uiState.sistolicaText.isBlank() ||
        uiState.diastolicaText.isBlank() ||
        uiState.saturacionText.isBlank() ||
        uiState.horasSuenoText.isBlank() ||
        uiState.horasUltimoAlcoholText.isBlank()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopAppBar(
                title = "Evaluación Médica de Salud",
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
            // Header Info Card
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Buzo: ${uiState.currentBuzo?.nombre ?: "Sin Asignar"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
                Text(
                    text = "Ingrese cada parámetro fisiológico en los campos correspondientes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Live Banner Status Handling
            if (hasBlankFields) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF3E0), shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "EVALUACIÓN PENDIENTE DE INGRESO",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = "Por favor rellenar todos los campos para validar aptitud.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }
            } else if (!uiState.saludApto) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ObservadoContainerRed, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = null,
                                tint = ObservadoRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "INHABILITADO / REQUIERE REVISIÓN",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ObservadoRed
                            )
                        }
                        Text(
                            text = "Criterios médicos no cumplidos:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = ObservadoRed
                        )
                        uiState.mensajesValidacionSalud.forEach { mensaje ->
                            Text(
                                text = "• $mensaje",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = ObservadoRed
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AptoContainerGreen, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = OnAptoContainerGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "EVALUACIÓN MÉDICA: APTO",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnAptoContainerGreen
                            )
                            Text(
                                text = "Todos los parámetros médicos dentro del rango permitido.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnAptoContainerGreen
                            )
                        }
                    }
                }
            }

            // Section 1: Signos Vitales
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "1. Signos Vitales",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                NumericInputField(
                    label = "Frecuencia Cardíaca en reposo",
                    value = uiState.frecuenciaCardiacaText,
                    onValueChange = onFrecuenciaCardiacaChange,
                    icon = Icons.Outlined.Favorite,
                    placeholder = "Frecuencia Cardíaca (lpm)"
                )

                Spacer(modifier = Modifier.height(12.dp))

                NumericInputField(
                    label = "Presión Arterial Sistólica",
                    value = uiState.sistolicaText,
                    onValueChange = onSistolicaChange,
                    icon = Icons.Outlined.Speed,
                    placeholder = "Presión Sistólica (mmHg)"
                )

                Spacer(modifier = Modifier.height(12.dp))

                NumericInputField(
                    label = "Presión Arterial Diastólica",
                    value = uiState.diastolicaText,
                    onValueChange = onDiastolicaChange,
                    icon = Icons.Outlined.Speed,
                    placeholder = "Presión Diastólica (mmHg)"
                )

                Spacer(modifier = Modifier.height(12.dp))

                NumericInputField(
                    label = "Saturación de Oxígeno SpO2",
                    value = uiState.saturacionText,
                    onValueChange = onSaturacionChange,
                    icon = Icons.Outlined.MonitorHeart,
                    placeholder = "Saturación SpO2 (%)"
                )
            }

            // Section 2: Descanso y Hábitos
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "2. Descanso y Hábitos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                NumericInputField(
                    label = "Horas de Sueño en las últimas 24h",
                    value = uiState.horasSuenoText,
                    onValueChange = onHorasSuenoChange,
                    icon = Icons.Outlined.Hotel,
                    placeholder = "Horas de Sueño (hrs)"
                )

                Spacer(modifier = Modifier.height(12.dp))

                NumericInputField(
                    label = "Horas desde el Último Consumo de Alcohol",
                    value = uiState.horasUltimoAlcoholText,
                    onValueChange = onHorasUltimoAlcoholChange,
                    icon = Icons.Outlined.LocalBar,
                    placeholder = "Horas sin Consumo de Alcohol (hrs)"
                )
            }

            // Section 3: Sintomatología y Estado Psicológico
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "3. Evaluación Sintomática y Psicológica",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                BooleanSelectionRow(
                    title = "Congestión Nasal / Sinusal",
                    selectedValue = uiState.tieneCongestionNasal,
                    onValueSelected = onCongestionNasalChange
                )

                Spacer(modifier = Modifier.height(12.dp))

                BooleanSelectionRow(
                    title = "Medicamentos Nuevos / Sedantes",
                    selectedValue = uiState.tomaMedicamentosNuevos,
                    onValueSelected = onMedicamentosNuevosChange
                )

                Spacer(modifier = Modifier.height(12.dp))

                BooleanSelectionRow(
                    title = "Estado Psicológico Calmo y Enfocado",
                    selectedValue = uiState.estadoPsicologicoCalmo,
                    onValueSelected = onEstadoPsicologicoCalmoChange,
                    positiveLabel = "Sí (Calmo)",
                    negativeLabel = "No (Alterado)"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = "Continuar al Checklist",
                icon = Icons.AutoMirrored.Rounded.ArrowForward,
                onClick = onContinuarClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun NumericInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() }) {
                onValueChange(newValue)
            }
        },
        label = { Text(label, color = MaterialTheme.colorScheme.onSurface) },
        placeholder = { Text(placeholder, color = Color.Gray) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryDarkGreen
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedBorderColor = PrimaryDarkGreen,
            unfocusedBorderColor = PrimaryDarkGreen,
            focusedLabelColor = PrimaryDarkGreen,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun BooleanSelectionRow(
    title: String,
    selectedValue: Boolean,
    onValueSelected: (Boolean) -> Unit,
    positiveLabel: String = "Sí",
    negativeLabel: String = "No"
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedValue,
                onClick = { onValueSelected(true) },
                label = {
                    Text(
                        text = positiveLabel,
                        fontWeight = if (selectedValue) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryDarkGreen,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            FilterChip(
                selected = !selectedValue,
                onClick = { onValueSelected(false) },
                label = {
                    Text(
                        text = negativeLabel,
                        fontWeight = if (!selectedValue) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryDarkGreen,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SaludScreenPreview() {
    AquaCheckTheme {
        SaludContent(
            uiState = PreChequeoUiState(
                currentBuzo = MockData.mockBuzos[0]
            ),
            onBackClick = {},
            onFrecuenciaCardiacaChange = {},
            onSistolicaChange = {},
            onDiastolicaChange = {},
            onSaturacionChange = {},
            onHorasSuenoChange = {},
            onHorasUltimoAlcoholChange = {},
            onCongestionNasalChange = {},
            onMedicamentosNuevosChange = {},
            onEstadoPsicologicoCalmoChange = {},
            onContinuarClick = {}
        )
    }
}
