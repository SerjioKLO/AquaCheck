package com.example.aquacheck.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aquacheck.ui.theme.AptoContainerGreen
import com.example.aquacheck.ui.theme.AquaCheckTheme
import com.example.aquacheck.ui.theme.OnAptoContainerGreen
import com.example.aquacheck.ui.theme.ObservadoContainerRed
import com.example.aquacheck.ui.theme.ObservadoRed

@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val cleanStatus = status.trim().lowercase()

    val isPositive = cleanStatus == "apto" || cleanStatus == "cumple" || cleanStatus == "apto / cumple"
    val isWarning = cleanStatus == "observado" || cleanStatus == "atención" || cleanStatus == "atencion"
    val isNegative = cleanStatus.contains("no cumple") || cleanStatus.contains("inhabilitado") || cleanStatus.contains("requiere revisión") || cleanStatus.contains("requiere revision") || cleanStatus.contains("crítica") || cleanStatus.contains("critica")
    val isNoAplica = cleanStatus == "no aplica" || cleanStatus == "n/a"

    val containerColor = when {
        isPositive -> AptoContainerGreen
        isWarning -> ObservadoContainerRed
        isNegative -> ObservadoContainerRed
        isNoAplica -> Color(0xFFE2E8F0)
        else -> AptoContainerGreen
    }

    val contentColor = when {
        isPositive -> OnAptoContainerGreen
        isWarning -> ObservadoRed
        isNegative -> ObservadoRed
        isNoAplica -> androidx.compose.ui.graphics.Color(0xFF475569)
        else -> OnAptoContainerGreen
    }

    val icon = when {
        isPositive -> Icons.Rounded.CheckCircle
        isWarning || isNegative -> Icons.Rounded.Warning
        else -> Icons.Rounded.CheckCircle
    }

    val displayText = status.ifBlank { "Apto" }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = displayText,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = displayText,
            color = contentColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatusChipPreview() {
    AquaCheckTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusChip(status = "APTO")
            StatusChip(status = "OBSERVADO")
        }
    }
}
