package cz.myapp.tvguide.presentation.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.myapp.tvguide.di.DomainModule
import cz.myapp.tvguide.presentation.screens.HomeScreen
import cz.myapp.tvguide.presentation.screens.detail.DetailScreen

/**
 * Home screen wrapper that provides navigation support.
 * ScreenModel is created here to persist across tab switches.
 */
object HomeScreenWrapper : Screen {
    
    // Screen-level ScreenModel - persists in Navigator even when tab is disposed
    private val screenModel by lazy {
        HomeScreenModel(
            getCurrentProgramsUseCase = DomainModule.getCurrentProgramsUseCase,
            getFavoriteChannelsUseCase = DomainModule.getFavoriteChannelsUseCase
        )
    }
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val state by screenModel.state.collectAsState()
        
        HomeScreen(
            state = state,
            onRefresh = { screenModel.refresh() },
            onProgramClick = { programId ->
                navigator.push(DetailScreen(programId))
            }
        )
    }
}
