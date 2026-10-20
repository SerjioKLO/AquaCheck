package com.example.aquacheck.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aquacheck.model.MockData
import com.example.aquacheck.model.PreChequeo
import com.example.aquacheck.model.RolUsuario
import com.example.aquacheck.ui.components.AppCard
import com.example.aquacheck.ui.components.AppNavigationBar
import com.example.aquacheck.ui.components.AppTopAppBar
import com.example.aquacheck.ui.components.NavigationTab
import com.example.aquacheck.ui.components.StatusChip
import com.example.aquacheck.ui.theme.AptoContainerGreen
import com.example.aquacheck.ui.theme.AquaCheckTheme
import com.example.aquacheck.ui.theme.BackgroundLight
import com.example.aquacheck.ui.theme.OnPrimaryWhite
import com.example.aquacheck.ui.theme.PrimaryDarkGreen
import com.example.aquacheck.viewmodel.PreChequeoUiState
import com.example.aquacheck.viewmodel.PreChequeoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InicioScreen(
    viewModel: PreChequeoViewModel,
    onNewPreChequeoClick: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    InicioContent(
        uiState = uiState,
        onSearchQueryChange = { viewModel.setSearchQuery(it) },
        onFilterSelected = { viewModel.setSelectedFilter(it) },
        onNewPreChequeoClick = onNewPreChequeoClick,
        onNavigateToPerfil = onNavigateToPerfil,
        onLogoutClick = {
            viewModel.logout()
            onLogoutClick()
        },
        modifier = modifier
    )
}

@Composable
fun InicioContent(
    uiState: PreChequeoUiState,
    onSearchQueryChange: (String) -> Unit,
    onFilterSelected: (String) -> Unit,
    onNewPreChequeoClick: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var isSyncing by remember { mutableStateOf(false) }
    var syncProgress by remember { mutableFloatStateOf(0f) }
    var filterMenuExpanded by remember { mutableStateOf(false) }

    val navigationTabs = listOf(
        NavigationTab("Historial", Icons.Rounded.History),
        NavigationTab("Sincronizar", Icons.Rounded.Sync),
        NavigationTab("Perfil", Icons.Rounded.AccountCircle)
    )

    fun startSyncProcess() {
        if (isSyncing) return
        isSyncing = true
        syncProgress = 0f
        coroutineScope.launch {
            for (i in 1..100) {
                delay(20)
                syncProgress = i / 100f
            }
            delay(200)
            isSyncing = false
            snackbarHostState.showSnackbar("Sincronizacion completada")
        }
    }

    val isSupervisor = uiState.currentUser?.rol == RolUsuario.SUPERVISOR

    val filteredPreChequeos = uiState.preChequeos.filter { preChequeo ->
        val matchesSearch = uiState.searchQuery.isBlank() ||
            preChequeo.buzo.nombre.contains(uiState.searchQuery, ignoreCase = true) ||
            preChequeo.centro.nombre.contains(uiState.searchQuery, ignoreCase = true) ||
            preChequeo.fecha.contains(uiState.searchQuery, ignoreCase = true)

        val matchesFilter = when (uiState.selectedFilter) {
            "Apto" -> preChequeo.resultado.equals("Apto", ignoreCase = true)
            "Observado" -> preChequeo.resultado.equals("Observado", ignoreCase = true)
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopAppBar(
                title = "AquaCheck",
                actions = {
                    TextButton(onClick = onLogoutClick) {
                        Text(
                            text = "Salir",
                            color = OnPrimaryWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                AnimatedVisibility(
                    visible = isSyncing,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BackgroundLight)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Sincronizando...",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryDarkGreen
                            )
                            Text(
                                text = "${(syncProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarkGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { syncProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = PrimaryDarkGreen,
                            trackColor = AptoContainerGreen
                        )
                    }
                }

                AppNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { index ->
                        if (index == 1) {
                            startSyncProcess()
                        } else if (index == 2) {
                            onNavigateToPerfil()
                        } else {
                            selectedTab = index
                        }
                    },
                    tabs = navigationTabs
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 0 && isSupervisor) {
                FloatingActionButton(
                    onClick = onNewPreChequeoClick,
                    containerColor = PrimaryDarkGreen,
                    contentColor = OnPrimaryWhite,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Nuevo Pre-Chequeo"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
        ) {
            when (selectedTab) {
                0, 1 -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = uiState.searchQuery,
                                onValueChange = onSearchQueryChange,
                                label = { Text("Buscar", color = MaterialTheme.colorScheme.onSurface) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = null,
                                        tint = PrimaryDarkGreen
                                    )
                                },
                                trailingIcon = {
                                    Box {
                                        IconButton(onClick = { filterMenuExpanded = true }) {
                                            Icon(
                                                imageVector = Icons.Rounded.FilterList,
                                                contentDescription = "Filtrar por estado",
                                                tint = PrimaryDarkGreen
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = filterMenuExpanded,
                                            onDismissRequest = { filterMenuExpanded = false }
                                        ) {
                                            listOf("Todos", "Apto", "Observado").forEach { filter ->
                                                DropdownMenuItem(
                                                    text = { Text(text = "$filter", color = MaterialTheme.colorScheme.onSurface) },
                                                    onClick = {
                                                        onFilterSelected(filter)
                                                        filterMenuExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
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
                        }

                        HistorialTab(
                            preChequeos = filteredPreChequeos,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun HistorialTab(
    preChequeos: List<PreChequeo>,
    modifier: Modifier = Modifier
) {
    if (preChequeos.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No se encontraron registros de pre-chequeos.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(preChequeos, key = { it.id }) { preChequeo ->
                PreChequeoCard(preChequeo = preChequeo)
            }
        }
    }
}

@Composable
private fun PreChequeoCard(
    preChequeo: PreChequeo,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fecha: ${preChequeo.fecha}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkGreen
            )
            StatusChip(status = preChequeo.resultado)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Buzo: ${preChequeo.buzo.nombre}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Supervisor: ${preChequeo.supervisor.nombre}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Centro: ${preChequeo.centro.nombre}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InicioScreenPreview() {
    AquaCheckTheme {
        InicioContent(
            uiState = PreChequeoUiState(preChequeos = MockData.mockPreChequeos),
            onSearchQueryChange = {},
            onFilterSelected = {},
            onNewPreChequeoClick = {},
            onNavigateToPerfil = {},
            onLogoutClick = {}
        )
    }
}
