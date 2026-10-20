package com.example.aquacheck.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Anchor
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.SupervisorAccount
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aquacheck.model.Buzo
import com.example.aquacheck.model.Centro
import com.example.aquacheck.model.MockData
import com.example.aquacheck.model.Supervisor
import com.example.aquacheck.ui.components.AppCard
import com.example.aquacheck.ui.components.AppTopAppBar
import com.example.aquacheck.ui.components.PrimaryButton
import com.example.aquacheck.ui.theme.AquaCheckTheme
import com.example.aquacheck.ui.theme.BackgroundLight
import com.example.aquacheck.ui.theme.PrimaryDarkGreen
import com.example.aquacheck.viewmodel.PreChequeoUiState
import com.example.aquacheck.viewmodel.PreChequeoViewModel

@Composable
fun PreChequeoScreen(
    viewModel: PreChequeoViewModel,
    onBackClick: () -> Unit,
    onNavigateToSalud: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PreChequeoContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onContinuar = { centro, fecha, buzo, supervisor ->
            viewModel.setFormData(centro = centro, fecha = fecha, buzo = buzo, supervisor = supervisor)
            onNavigateToSalud()
        },
        modifier = modifier
    )
}

@Composable
fun PreChequeoContent(
    uiState: PreChequeoUiState,
    onBackClick: () -> Unit,
    onContinuar: (Centro?, String, Buzo?, Supervisor?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCentro by remember(uiState.currentCentro) {
        mutableStateOf(uiState.currentCentro ?: MockData.mockCentros.firstOrNull())
    }
    var fechaText by remember(uiState.currentDate) {
        mutableStateOf(uiState.currentDate)
    }
    var selectedBuzo by remember(uiState.currentBuzo) {
        mutableStateOf(uiState.currentBuzo ?: MockData.mockBuzos.firstOrNull())
    }
    var selectedSupervisor by remember(uiState.currentSupervisor) {
        mutableStateOf(uiState.currentSupervisor ?: MockData.mockSupervisores.firstOrNull())
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopAppBar(
                title = "Nuevo Pre-Chequeo",
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
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Datos Generales",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Seleccione los datos para iniciar la verificación pre-inmersión.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Centro Selection
            DropdownSelectField(
                label = "Centro de Cultivo / Operación",
                selectedText = selectedCentro?.nombre ?: "Seleccionar Centro",
                icon = Icons.Rounded.Place,
                options = MockData.mockCentros,
                optionLabel = { it.nombre },
                onOptionSelected = {
                    selectedCentro = it
                    errorMessage = null
                }
            )

            // Fecha Text Field
            OutlinedTextField(
                value = fechaText,
                onValueChange = {
                    fechaText = it
                    errorMessage = null
                },
                label = { Text("Fecha de Inspección", color = MaterialTheme.colorScheme.onSurface) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.CalendarToday,
                        contentDescription = null,
                        tint = PrimaryDarkGreen
                    )
                },
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

            // Buzo Selection
            DropdownSelectField(
                label = "Buzo Inspeccionado",
                selectedText = selectedBuzo?.nombre ?: "Seleccionar Buzo",
                icon = Icons.Rounded.Badge,
                options = MockData.mockBuzos,
                optionLabel = { "${it.nombre} (${it.certificacion})" },
                onOptionSelected = {
                    selectedBuzo = it
                    errorMessage = null
                }
            )

            // Supervisor Selection
            DropdownSelectField(
                label = "Supervisor Responsable",
                selectedText = selectedSupervisor?.nombre ?: "Seleccionar Supervisor",
                icon = Icons.Rounded.SupervisorAccount,
                options = MockData.mockSupervisores,
                optionLabel = { it.nombre },
                onOptionSelected = {
                    selectedSupervisor = it
                    errorMessage = null
                }
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "Continuar a Salud",
                icon = Icons.Rounded.Anchor,
                onClick = {
                    if (selectedCentro == null || selectedBuzo == null || selectedSupervisor == null || fechaText.isBlank()) {
                        errorMessage = "Por favor complete todos los campos obligatorios."
                    } else {
                        errorMessage = null
                        onContinuar(selectedCentro, fechaText, selectedBuzo, selectedSupervisor)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DropdownSelectField(
    label: String,
    selectedText: String,
    icon: ImageVector,
    options: List<T>,
    optionLabel: (T) -> String,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = MaterialTheme.colorScheme.onSurface) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryDarkGreen
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = PrimaryDarkGreen,
                unfocusedBorderColor = PrimaryDarkGreen,
                focusedLabelColor = PrimaryDarkGreen,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = optionLabel(option), color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreChequeoScreenPreview() {
    AquaCheckTheme {
        PreChequeoContent(
            uiState = PreChequeoUiState(
                currentBuzo = MockData.mockBuzos[0],
                currentSupervisor = MockData.mockSupervisores[0],
                currentCentro = MockData.mockCentros[0],
                currentDate = "2026-10-05"
            ),
            onBackClick = {},
            onContinuar = { _, _, _, _ -> }
        )
    }
}
