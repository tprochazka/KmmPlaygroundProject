package cz.myapp.tvguide.presentation.navigation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cz.myapp.tvguide.presentation.screens.EpgScreen
import cz.myapp.tvguide.presentation.components.LocalNavigationVisible

/**
 * EPG tab - Shows program guide grid (US3)
 */
object EpgTab : Tab {
    
    override val options: TabOptions
        @Composable
        get() {
            val title = "Mřížka"
            val icon = rememberVectorPainter(Icons.Default.DateRange)
            
            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }
    
    @Composable
    override fun Content() {
        Navigator(EpgScreen) { navigator ->
            val navigationVisible = LocalNavigationVisible.current
            
            // Update navigation visibility based on stack size
            LaunchedEffect(navigator.size) {
                navigationVisible.value = navigator.size <= 1
            }
            
            navigator.lastItem.Content()
        }
    }
}
