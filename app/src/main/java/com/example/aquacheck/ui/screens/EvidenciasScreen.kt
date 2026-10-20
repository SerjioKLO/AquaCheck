package com.example.aquacheck.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.aquacheck.model.EvidenciaItem
import com.example.aquacheck.ui.components.AppTopAppBar
import com.example.aquacheck.ui.components.PrimaryButton
import com.example.aquacheck.ui.theme.AquaCheckTheme
import com.example.aquacheck.ui.theme.BackgroundLight
import com.example.aquacheck.ui.theme.OnBackgroundText
import com.example.aquacheck.ui.theme.OutlineBorder
import com.example.aquacheck.ui.theme.SurfaceLight
import com.example.aquacheck.viewmodel.PreChequeoUiState
import com.example.aquacheck.viewmodel.PreChequeoViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun EvidenciasScreen(
    viewModel: PreChequeoViewModel,
    onBackClick: () -> Unit,
    onNavigateToResumen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EvidenciasContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onAddEvidencia = { evidencia ->
            viewModel.addEvidencia(evidencia)
        },
        onRemoveEvidencia = { id ->
            viewModel.removeEvidencia(id)
        },
        onSiguienteClick = {
            viewModel.calcularResultado()
            onNavigateToResumen()
        },
        modifier = modifier
    )
}

@Composable
fun EvidenciasContent(
    uiState: PreChequeoUiState,
    onBackClick: () -> Unit,
    onAddEvidencia: (EvidenciaItem) -> Unit,
    onRemoveEvidencia: (String) -> Unit,
    onSiguienteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var pendingBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var evidenciaTitulo by remember { mutableStateOf("") }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            pendingBitmap = bitmap
            evidenciaTitulo = "Evidencia #${uiState.evidencias.size + 1}"
            showCategoryDialog = true
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        try {
            cameraLauncher.launch(null)
        } catch (e: Exception) {
            pendingBitmap = createSampleBitmap()
            evidenciaTitulo = "Evidencia #${uiState.evidencias.size + 1}"
            showCategoryDialog = true
        }
    }

    fun launchCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                pendingBitmap = createSampleBitmap()
                evidenciaTitulo = "Evidencia #${uiState.evidencias.size + 1}"
                showCategoryDialog = true
            }
        } else {
            try {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            } catch (e: Exception) {
                pendingBitmap = createSampleBitmap()
                evidenciaTitulo = "Evidencia #${uiState.evidencias.size + 1}"
                showCategoryDialog = true
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopAppBar(
                title = "Evidencias",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceLight)
                    .padding(16.dp)
            ) {
                PrimaryButton(
                    text = "Siguiente",
                    onClick = onSiguienteClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Registra el estado del equipo y el área de trabajo.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (uiState.evidencias.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            tint = OutlineBorder,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "No hay evidencias fotográficas registradas.\nPresiona \"Capturar\" para abrir la cámara del teléfono y tomar una foto.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.evidencias, key = { it.id }) { evidencia ->
                        EvidenciaCardItem(
                            evidencia = evidencia,
                            onDeleteClick = { onRemoveEvidencia(evidencia.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { launchCamera() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.5.dp, OutlineBorder)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Capturar Evidencia",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (showCategoryDialog && pendingBitmap != null) {
        val presetCategories = listOf(
            "Compresor",
            "Oxígeno",
            "Área de inmersión",
            "Traje de buceo",
            "Equipo de Buceo"
        )

        AlertDialog(
            onDismissRequest = {
                showCategoryDialog = false
                pendingBitmap = null
            },
            title = {
                Text(
                    text = "Título de Evidencia",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Selecciona o escribe el nombre del equipo o área fotografiada:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Preset tags row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetCategories.take(2).forEach { cat ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .clickable { evidenciaTitulo = cat }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Preset tags row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetCategories.drop(2).take(2).forEach { cat ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .clickable { evidenciaTitulo = cat }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = evidenciaTitulo,
                        onValueChange = { evidenciaTitulo = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Nombre / Categoría") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val bitmapToSave = pendingBitmap
                        if (bitmapToSave != null) {
                            val savedPath = saveBitmapToSystemStorage(context, bitmapToSave)
                            val finalTitle = if (evidenciaTitulo.isBlank()) {
                                "Evidencia #${uiState.evidencias.size + 1}"
                            } else evidenciaTitulo

                            val nuevaEvidencia = EvidenciaItem(
                                titulo = finalTitle,
                                imagePath = savedPath,
                                bitmap = bitmapToSave
                            )
                            onAddEvidencia(nuevaEvidencia)
                        }
                        showCategoryDialog = false
                        pendingBitmap = null
                    }
                ) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCategoryDialog = false
                        pendingBitmap = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun EvidenciaCardItem(
    evidencia: EvidenciaItem,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
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
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Delete Button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { onDeleteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Eliminar",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = evidencia.titulo,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnBackgroundText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun createSampleBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        color = android.graphics.Color.rgb(18, 38, 14)
        isAntiAlias = true
    }
    canvas.drawRect(0f, 0f, 400f, 300f, paint)

    val textPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 28f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("Foto Capturada", 200f, 150f, textPaint)
    return bitmap
}

private fun saveBitmapToSystemStorage(context: Context, bitmap: Bitmap): String {
    val directory = File(context.filesDir, "evidencias")
    if (!directory.exists()) {
        directory.mkdirs()
    }
    val file = File(directory, "evidencia_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return file.absolutePath
}

@Preview(showBackground = true)
@Composable
fun EvidenciasScreenPreview() {
    AquaCheckTheme {
        EvidenciasContent(
            uiState = PreChequeoUiState(),
            onBackClick = {},
            onAddEvidencia = {},
            onRemoveEvidencia = {},
            onSiguienteClick = {}
        )
    }
}
