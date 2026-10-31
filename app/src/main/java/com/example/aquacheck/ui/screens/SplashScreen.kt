package com.example.aquacheck.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aquacheck.R
import com.example.aquacheck.ui.theme.AptoContainerGreen
import com.example.aquacheck.ui.theme.PrimaryDarkGreen
import com.example.aquacheck.viewmodel.PreChequeoViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: PreChequeoViewModel,
    onNavigateToDashboard: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        // Animate progress bar over ~1.8 seconds
        for (i in 1..100) {
            delay(18)
            progress = i / 100f
        }
        delay(100)
        if (uiState.currentUser != null) {
            onNavigateToDashboard()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8F4))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_aquacheck),
                contentDescription = "Logo AquaCheck",
                modifier = Modifier.size(130.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "AquaCheck",
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight(50),
                    fontSize = 34.sp,
                    letterSpacing = 6.sp,
                    color = PrimaryDarkGreen
                ),
                textAlign = TextAlign.Center
            )
        }

        // Loading bar at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.6f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = PrimaryDarkGreen,
                    trackColor = AptoContainerGreen
                )
            }
        }
    }
}
