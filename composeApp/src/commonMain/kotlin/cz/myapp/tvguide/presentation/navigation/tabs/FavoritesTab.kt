package cz.myapp.tvguide.presentation.navigation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.navigator.Navigator
import androidx.compose.runtime.LaunchedEffect
import cz.myapp.tvguide.presentation.screens.FavoritesScreen
import cz.myapp.tvguide.presentation.components.LocalNavigationVisible

/**
 * Favorites tab - Shows favorite programs and custom channel lists (US2)
 */
object FavoritesTab : Tab {
    
    override val options: TabOptions
        @Composable
        get() {
            val title = "Stanice"
            val icon = rememberVectorPainter(Icons.Default.Favorite)
            
            return remember {
                TabOptions(
                    index = 3u,
                    title = title,
                    icon = icon
                )
            }
        }
    
    @Composable
    override fun Content() {
        Navigator(FavoritesScreen) { navigator ->
            val navigationVisible = LocalNavigationVisible.current
            LaunchedEffect(navigator.size) {
                navigationVisible.value = navigator.size <= 1
            }
            navigator.lastItem.Content()
        }
    }
}
