package cz.myapp.tvguide.presentation.screens.home

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.myapp.tvguide.presentation.screens.HomeScreen
import cz.myapp.tvguide.presentation.screens.detail.DetailScreen

/**
 * Home screen wrapper that provides navigation support.
 */
object HomeScreenWrapper : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        HomeScreen(
            onProgramClick = { programId ->
                navigator.push(DetailScreen(programId))
            }
        )
    }
}
