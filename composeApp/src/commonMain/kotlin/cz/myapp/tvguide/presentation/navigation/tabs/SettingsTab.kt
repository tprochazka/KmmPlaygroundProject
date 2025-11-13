// Deprecated: SettingsTab is no longer used. Settings opens as pushed Screen instead of a Tab.
package cz.myapp.tvguide.presentation.navigation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cz.myapp.tvguide.presentation.screens.settings.SettingsScreen
import cz.myapp.tvguide.presentation.components.LocalNavigationVisible

/**
 * Settings tab - Shows app settings and theme toggle (US10)
 */
@Deprecated("SettingsTab is no longer used")
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
        Navigator(SettingsScreen()) { navigator ->
            val navigationVisible = LocalNavigationVisible.current
            
            // Update navigation visibility based on stack size
            // Settings is always the root screen of this tab, so hide navigation when on settings
            LaunchedEffect(navigator.size) {
                navigationVisible.value = false
            }
            
            navigator.lastItem.Content()
        }
    }
}
