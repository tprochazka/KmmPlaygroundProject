package cz.myapp.tvguide

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cz.myapp.tvguide.di.DataModule
import cz.myapp.tvguide.presentation.components.AdaptiveScaffold
import cz.myapp.tvguide.presentation.navigation.tabs.*
import cz.myapp.tvguide.presentation.theme.ThemeMode
import cz.myapp.tvguide.presentation.theme.TvGuideTheme

/**
 * Main application composable.
 *
 * Provides:
 * - Theme management from UserPreferencesRepository
 * - Tab-based navigation (Voyager TabNavigator)
 * - Adaptive scaffold (bottom bar / nav rail based on screen size)
 */
@Composable
fun App() {
    // Access theme mode from UserPreferencesRepository
    val userPreferencesRepo = remember { DataModule.userPreferencesRepository }
    val userPreferences by userPreferencesRepo.getUserPreferences().collectAsState(initial = null)
    val themeMode = userPreferences?.themeMode ?: ThemeMode.SYSTEM

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
