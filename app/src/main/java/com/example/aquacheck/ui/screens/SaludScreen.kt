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
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.aquacheck.model.NivelSalud
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
        onSistolicaChange = { viewModel.setSistolica(it) },
        onDiastolicaChange = { viewModel.setDiastolica(it) },
        onSaturacionChange = { viewModel.setSaturacion(it) },
        onFrecuenciaCardiacaChange = { viewModel.setFrecuenciaCardiaca(it) },
        onHorasSuenoChange = { viewModel.setHorasSueno(it) },
        onHorasUltimoAlcoholChange = { viewModel.setHorasUltimoAlcohol(it) },
        onDurmioMenos6hrsChange = { viewModel.setDurmioMenos6hrs(it) },
        onResfrioDolorOidosChange = { viewModel.setResfrioDolorOidos(it) },
        onConsumioAlcohol24hrsChange = { viewModel.setConsumioAlcohol24hrs(it) },
        onContinuarClick = {
            onNavigateToChecklist()
        },
        modifier = modifier
    )
}

@Composable
fun SaludContent(
    uiState: PreChequeoUiState,
    onBackClick: () -> Unit,
    onSistolicaChange: (String) -> Unit,
    onDiastolicaChange: (String) -> Unit,
    onSaturacionChange: (String) -> Unit,
    onFrecuenciaCardiacaChange: (String) -> Unit,
    onHorasSuenoChange: (String) -> Unit,
    onHorasUltimoAlcoholChange: (String) -> Unit,
    onDurmioMenos6hrsChange: (Boolean) -> Unit,
    onResfrioDolorOidosChange: (Boolean) -> Unit,
    onConsumioAlcohol24hrsChange: (Boolean) -> Unit,
    onContinuarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopAppBar(
                title = "Evaluación de Salud del Buzo",
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
                    text = "Complete todos los parámetros fisiológicos, horas de sueño, alcohol y responda la encuesta para continuar.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Live Health Traffic Light Banner (Semáforo de Salud en Vivo)
            val nivelSalud = uiState.nivelSalud
            if (nivelSalud == null) {
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
                                text = "SEMÁFORO DE SALUD EN VIVO",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = "Ingrese sistólica, diastólica y saturación O₂ para calcular el nivel.",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }
            } else {
                when (nivelSalud) {
                    NivelSalud.NORMAL -> {
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
                                    modifier = Modifier.size(32.dp)
                                )
                                Column {
                                    Text(
                                        text = "🟢 SALUD NORMAL",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = OnAptoContainerGreen
                                    )
                                    Text(
                                        text = "Todos los parámetros en rango normal. Apto para inmersión.",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = OnAptoContainerGreen
                                    )
                                }
                            }
                        }
                    }
                    NivelSalud.ATENCION -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF59D), shape = RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFF57F17),
                                    modifier = Modifier.size(32.dp)
                                )
                                Column {
                                    Text(
                                        text = "🟡 SALUD EN ATENCIÓN",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF57F17)
                                    )
                                    Text(
                                        text = "Presión elevada, saturación levemente baja o alerta en la encuesta.",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF57F17)
                                    )
                                }
                            }
                        }
                    }
                    NivelSalud.CRITICA -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObservadoContainerRed, shape = RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Warning,
                                    contentDescription = null,
                                    tint = ObservadoRed,
                                    modifier = Modifier.size(32.dp)
                                )
                                Column {
                                    Text(
                                        text = "🔴 SALUD CRÍTICA",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ObservadoRed
                                    )
                                    Text(
                                        text = "Saturación < 92% o presión crítica. Inhabilitado para buceo.",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ObservadoRed
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 1: Mediciones Fisiológicas
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "1. Mediciones Fisiológicas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                NumericInputField(
                    label = "Presión Arterial Sistólica (mmHg) *",
                    value = uiState.sistolicaText,
                    onValueChange = onSistolicaChange,
                    icon = Icons.Outlined.Speed,
                    placeholder = "Ej: 120"
                )

                Spacer(modifier = Modifier.height(12.dp))

                NumericInputField(
                    label = "Presión Arterial Diastólica (mmHg) *",
                    value = uiState.diastolicaText,
                    onValueChange = onDiastolicaChange,
                    icon = Icons.Outlined.Speed,
                    placeholder = "Ej: 80"
                )

                Spacer(modifier = Modifier.height(12.dp))

                NumericInputField(
                    label = "Saturación de O₂ (%) *",
                    value = uiState.saturacionText,
                    onValueChange = onSaturacionChange,
                    icon = Icons.Outlined.MonitorHeart,
                    placeholder = "Ej: 98"
                )

                Spacer(modifier = Modifier.height(12.dp))

                NumericInputField(
                    label = "Frecuencia Cardíaca en Reposo (lpm) *",
                    value = uiState.frecuenciaCardiacaText,
                    onValueChange = onFrecuenciaCardiacaChange,
                    icon = Icons.Outlined.MonitorHeart,
                    placeholder = "Ej: 72"
                )
            }

            // Section 2: Antecedentes de Sueño y Alcohol
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "2. Antecedentes y Sueño / Alcohol",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                NumericInputField(
                    label = "Horas de sueño en las últimas 24 hrs *",
                    value = uiState.horasSuenoText,
                    onValueChange = onHorasSuenoChange,
                    icon = Icons.Outlined.Speed,
                    placeholder = "Ej: 7"
                )

                Spacer(modifier = Modifier.height(12.dp))

                NumericInputField(
                    label = "Horas desde el último consumo de alcohol *",
                    value = uiState.horasUltimoAlcoholText,
                    onValueChange = onHorasUltimoAlcoholChange,
                    icon = Icons.Outlined.Speed,
                    placeholder = "Ej: 48"
                )
            }

            // Section 3: Encuesta de Salud (3 Preguntas)
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "3. Encuesta de Salud",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                BooleanSelectionRow(
                    title = "¿Durmió menos de 6 horas?",
                    selectedValue = uiState.durmioMenos6hrs,
                    onValueSelected = onDurmioMenos6hrsChange
                )

                Spacer(modifier = Modifier.height(12.dp))

                BooleanSelectionRow(
                    title = "¿Resfrío o dolor de oídos?",
                    selectedValue = uiState.resfrioDolorOidos,
                    onValueSelected = onResfrioDolorOidosChange
                )

                Spacer(modifier = Modifier.height(12.dp))

                BooleanSelectionRow(
                    title = "¿Consumió alcohol en las últimas 24 horas?",
                    selectedValue = uiState.consumioAlcohol24hrs,
                    onValueSelected = onConsumioAlcohol24hrsChange
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = "Continuar al Checklist",
                icon = Icons.AutoMirrored.Rounded.ArrowForward,
                onClick = {
                    if (uiState.sistolicaText.isBlank() ||
                        uiState.diastolicaText.isBlank() ||
                        uiState.saturacionText.isBlank() ||
                        uiState.frecuenciaCardiacaText.isBlank() ||
                        uiState.horasSuenoText.isBlank() ||
                        uiState.horasUltimoAlcoholText.isBlank()
                    ) {
                        errorMessage = "Por favor complete todos los campos obligatorios (*): Presión, Saturación, Frecuencia Cardíaca, Horas de Sueño y Horas de Alcohol."
                    } else {
                        errorMessage = null
                        onContinuarClick()
                    }
                },
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
        label = { Text(label, color = Color.Black, fontWeight = FontWeight.Bold) },
        placeholder = { Text(placeholder, color = Color.DarkGray, fontWeight = FontWeight.Bold) },
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
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedBorderColor = PrimaryDarkGreen,
            unfocusedBorderColor = PrimaryDarkGreen,
            focusedLabelColor = PrimaryDarkGreen,
            unfocusedLabelColor = Color.Black
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
            color = Color.Black
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (selectedValue) MaterialTheme.colorScheme.onPrimary else Color.Black
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (!selectedValue) MaterialTheme.colorScheme.onPrimary else Color.Black
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
            onSistolicaChange = {},
            onDiastolicaChange = {},
            onSaturacionChange = {},
            onFrecuenciaCardiacaChange = {},
            onHorasSuenoChange = {},
            onHorasUltimoAlcoholChange = {},
            onDurmioMenos6hrsChange = {},
            onResfrioDolorOidosChange = {},
            onConsumioAlcohol24hrsChange = {},
            onContinuarClick = {}
        )
    }
}
