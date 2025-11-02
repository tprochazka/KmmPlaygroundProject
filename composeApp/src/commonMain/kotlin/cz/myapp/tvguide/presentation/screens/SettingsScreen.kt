package cz.myapp.tvguide.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.myapp.tvguide.getThemeMode
import cz.myapp.tvguide.setThemeMode
import cz.myapp.tvguide.presentation.theme.ThemeMode

/**
 * Settings screen - Shows app settings including theme toggle (US10)
 */
@Composable
fun SettingsScreen() {
    val currentTheme = getThemeMode()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Nastavení",
            style = MaterialTheme.typography.headlineMedium
        )
        
        HorizontalDivider()
        
        // Theme selection
        Text(
            text = "Téma aplikace",
            style = MaterialTheme.typography.titleMedium
        )
        
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeOption(
                label = "Systémové",
                selected = currentTheme == ThemeMode.SYSTEM,
                onClick = { setThemeMode(ThemeMode.SYSTEM) }
            )
            ThemeOption(
                label = "Světlé",
                selected = currentTheme == ThemeMode.LIGHT,
                onClick = { setThemeMode(ThemeMode.LIGHT) }
            )
            ThemeOption(
                label = "Tmavé",
                selected = currentTheme == ThemeMode.DARK,
                onClick = { setThemeMode(ThemeMode.DARK) }
            )
        }
    }
}

@Composable
private fun ThemeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
