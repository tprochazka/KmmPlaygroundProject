package cz.myapp.tvguide.presentation.navigation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cz.myapp.tvguide.presentation.screens.ListScreen

/**
 * List tab - Shows searchable program/channel lists (US4)
 */
object ListTab : Tab {
    
    override val options: TabOptions
        @Composable
        get() {
            val title = "Seznam"
            val icon = rememberVectorPainter(Icons.AutoMirrored.Filled.List)
            
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
        ListScreen()
    }
}
