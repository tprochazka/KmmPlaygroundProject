package cz.myapp.tvguide.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.myapp.tvguide.di.DomainModule
import cz.myapp.tvguide.presentation.theme.ThemeMode

/**
 * Settings screen for user preferences.
 * 
 * Allows users to customize:
 * - Theme mode (Light/Dark/System)
 * - Time format (12h/24h)
 * - Compact mode
 * - Notifications
 * 
 * US10: Theme & Navigation Customization
 */
data class SettingsScreen(
    val dummy: Unit = Unit
) : Screen {
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = SettingsScreenModel(
            getUserPreferencesUseCase = DomainModule.getUserPreferencesUseCase,
            updateUserPreferencesUseCase = DomainModule.updateUserPreferencesUseCase
        )
        
        val preferences by screenModel.preferences.collectAsState()
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Nastavení") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Zpět"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Theme Selection Section
                SettingsSection(title = "Vzhled") {
                    ThemeSelection(
                        selectedTheme = preferences.themeMode,
                        onThemeSelected = { screenModel.updateThemeMode(it) }
                    )
                }
                
                // Display Settings Section
                SettingsSection(title = "Zobrazení") {
                    SwitchPreference(
                        title = "24hodinový formát",
                        description = "Použít 24hodinový formát času",
                        checked = preferences.use24HourFormat,
                        onCheckedChange = { screenModel.toggleTimeFormat() }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    SwitchPreference(
                        title = "Kompaktní režim",
                        description = "Menší prvky a hustší rozložení",
                        checked = preferences.compactMode,
                        onCheckedChange = { screenModel.toggleCompactMode() }
                    )
                }
                
                // Notifications Section
                SettingsSection(title = "Oznámení") {
                    SwitchPreference(
                        title = "Povolit oznámení",
                        description = "Upozornění na oblíbené pořady",
                        checked = preferences.notificationsEnabled,
                        onCheckedChange = { screenModel.toggleNotifications() }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Theme selection radio button group.
 */
@Composable
private fun ThemeSelection(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ThemeMode.entries.forEach { theme ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedTheme == theme,
                        onClick = { onThemeSelected(theme) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedTheme == theme,
                    onClick = null // Handled by Row's selectable
                )
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = theme.displayName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = theme.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Switch preference row.
 */
@Composable
private fun SwitchPreference(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange() }
        )
    }
}

/**
 * Settings section with title and content.
 */
@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}
