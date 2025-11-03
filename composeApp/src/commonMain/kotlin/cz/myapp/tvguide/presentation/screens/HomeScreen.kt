package cz.myapp.tvguide.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cz.myapp.tvguide.di.DomainModule
import cz.myapp.tvguide.presentation.components.*
import cz.myapp.tvguide.presentation.screens.home.HomeScreenModel
import cz.myapp.tvguide.presentation.screens.home.HomeScreenState

/**
 * Home screen - Shows current TV programs (US1)
 * 
 * Features:
 * - Adaptive grid layout (1-3 columns based on screen size)
 * - Auto-refresh every minute
 * - Loading/empty/error states
 * - Live indicator and progress bars
 */
@Composable
fun HomeScreen(
    onProgramClick: (String) -> Unit = {}
) {
    // Create screen model with dependencies
    val screenModel = remember {
        HomeScreenModel(
            getCurrentProgramsUseCase = DomainModule.getCurrentProgramsUseCase,
            getFavoriteChannelsUseCase = DomainModule.getFavoriteChannelsUseCase
        )
    }
    
    val state: HomeScreenState by screenModel.state.collectAsState()
    
    // Determine grid columns based on window size
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val columns = when (adaptiveInfo.windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> 1    // Phone portrait
        WindowWidthSizeClass.MEDIUM -> 2     // Phone landscape / small tablet
        WindowWidthSizeClass.EXPANDED -> 3   // Tablet / desktop
        else -> 1
    }
    
    when (val currentState = state) {
        is HomeScreenState.Loading -> {
            LoadingIndicator(message = "Načítám programy...")
        }
        
        is HomeScreenState.Success -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = currentState.programs,
                    key = { (channel, _) -> channel.id }
                ) { (channel, program) ->
                    ProgramCard(
                        channel = channel,
                        program = program,
                        onClick = {
                            program?.let { onProgramClick(it.id) }
                        }
                    )
                }
            }
        }
        
        is HomeScreenState.Empty -> {
            EmptyState(message = currentState.message)
        }
        
        is HomeScreenState.Error -> {
            ErrorState(
                message = currentState.message,
                onRetry = { screenModel.refresh() }
            )
        }
    }
}
