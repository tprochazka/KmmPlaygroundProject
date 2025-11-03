package cz.myapp.tvguide.presentation.navigation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cz.myapp.tvguide.presentation.screens.home.HomeScreenWrapper

/**
 * Home tab - Shows current TV programs (US1)
 */
object HomeTab : Tab {
    
    override val options: TabOptions
        @Composable
        get() {
            val title = "Domů"
            val icon = rememberVectorPainter(Icons.Default.Home)
            
            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }
    
    @Composable
    override fun Content() {
        Navigator(HomeScreenWrapper)
    }
}
