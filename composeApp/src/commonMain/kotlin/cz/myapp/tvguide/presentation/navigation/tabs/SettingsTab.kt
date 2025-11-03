package cz.myapp.tvguide.presentation.navigation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cz.myapp.tvguide.presentation.screens.settings.SettingsScreen

/**
 * Settings tab - Shows app settings and theme toggle (US10)
 */
object SettingsTab : Tab {
    
    override val options: TabOptions
        @Composable
        get() {
            val title = "Nastavení"
            val icon = rememberVectorPainter(Icons.Default.Settings)
            
            return remember {
                TabOptions(
                    index = 4u,
                    title = title,
                    icon = icon
                )
            }
        }
    
    @Composable
    override fun Content() {
        SettingsScreen().Content()
    }
}
