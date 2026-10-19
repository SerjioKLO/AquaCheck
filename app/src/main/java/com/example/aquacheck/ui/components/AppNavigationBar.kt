package com.example.aquacheck.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aquacheck.ui.theme.AptoContainerGreen
import com.example.aquacheck.ui.theme.AquaCheckTheme
import com.example.aquacheck.ui.theme.PrimaryDarkGreen
import com.example.aquacheck.ui.theme.SurfaceLight

data class NavigationTab(
    val title: String,
    val icon: ImageVector
)

val defaultNavigationTabs = listOf(
    NavigationTab("Panel", Icons.Rounded.History),
    NavigationTab("Sincronización", Icons.Rounded.Sync),
    NavigationTab("Perfil", Icons.Rounded.AccountCircle)
)

@Composable
fun AppNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tabs: List<NavigationTab> = defaultNavigationTabs
) {
    NavigationBar(
        modifier = modifier,
        containerColor = SurfaceLight,
        tonalElevation = 8.dp
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = selectedTab == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryDarkGreen,
                    selectedTextColor = PrimaryDarkGreen,
                    indicatorColor = AptoContainerGreen,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationBarPreview() {
    AquaCheckTheme {
        AppNavigationBar(
            selectedTab = 0,
            onTabSelected = {}
        )
    }
}
