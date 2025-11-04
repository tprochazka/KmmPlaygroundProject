package cz.myapp.tvguide

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cz.myapp.tvguide.presentation.components.AdaptiveScaffold
import cz.myapp.tvguide.presentation.navigation.tabs.*
import cz.myapp.tvguide.presentation.theme.ThemeMode
import cz.myapp.tvguide.presentation.theme.TvGuideTheme

// Global theme state (simple approach for Phase 1 prototype)
// Phase 2+: Replace with proper state management (ViewModel or similar)
private var globalThemeMode = mutableStateOf(ThemeMode.SYSTEM)

fun getThemeMode(): ThemeMode = globalThemeMode.value
fun setThemeMode(mode: ThemeMode) {
    globalThemeMode.value = mode
}

/**
 * Main application composable.
 * 
 * Provides:
 * - Theme management (ThemeMode state)
 * - Tab-based navigation (Voyager TabNavigator)
 * - Adaptive scaffold (bottom bar / nav rail based on screen size)
 */
@Composable
fun App() {
    // Use global theme state
    val themeMode by globalThemeMode
    
    // Apply theme
    TvGuideTheme(themeMode = themeMode) {
        Surface {
            // Set up tab navigation with 4 main tabs (Settings moved to app bar menu)
            TabNavigator(HomeTab) {
                val tabs = remember {
                    listOf(
                        HomeTab,
                        EpgTab,
                        ListTab,
                        FavoritesTab
                    )
                }
                
                // Use adaptive scaffold for responsive navigation
                AdaptiveScaffold(tabs = tabs)
            }
        }
    }
}
