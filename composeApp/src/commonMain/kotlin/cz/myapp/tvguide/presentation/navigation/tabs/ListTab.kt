package cz.myapp.tvguide.presentation.navigation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cz.myapp.tvguide.presentation.screens.list.ListScreen
import cz.myapp.tvguide.presentation.components.LocalNavigationVisible

/**
 * List tab - Shows searchable program/channel lists (US4)
 */
object ListTab : Tab {
    
    // Screen instance persists at tab level
    private val listScreen = ListScreen()
    
    override val options: TabOptions
        @Composable
        get() {
            val title = "Seznam"
            val icon = rememberVectorPainter(Icons.AutoMirrored.Filled.List)
            
            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon
                )
            }
        }
    
    @Composable
    override fun Content() {
        Navigator(listScreen) { navigator ->
            val navigationVisible = LocalNavigationVisible.current
            
            // Update navigation visibility based on stack size
            LaunchedEffect(navigator.size) {
                navigationVisible.value = navigator.size <= 1
            }
            
            navigator.lastItem.Content()
        }
    }
}
