package cz.myapp.tvguide.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.myapp.tvguide.presentation.components.*
import cz.myapp.tvguide.presentation.screens.home.HomeScreenState
import cz.myapp.tvguide.presentation.screens.settings.SettingsScreen

/**
 * Home screen - Shows current TV programs (US1)
 * 
 * Features:
 * - Adaptive grid layout (1-3 columns based on screen size)
 * - Loading/empty/error states
 * - Live indicator and progress bars
 */
@Composable
fun HomeScreen(
    state: HomeScreenState,
    onRefresh: () -> Unit,
    onProgramClick: (String) -> Unit = {}
) {
    val navigator = LocalNavigator.currentOrThrow
    
    // Determine grid columns based on window size
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val columns = when (adaptiveInfo.windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> 1    // Phone portrait
        WindowWidthSizeClass.MEDIUM -> 2     // Phone landscape / small tablet
        WindowWidthSizeClass.EXPANDED -> 3   // Tablet / desktop
        else -> 1
    }
    
    Scaffold(
        topBar = {
            TVGuideAppBar(
                title = "Nyní v TV",
                onSettingsClick = {
                    navigator.push(SettingsScreen())
                }
            )
        }
    ) { paddingValues ->
        when (val currentState = state) {
            is HomeScreenState.Loading -> {
                LoadingIndicator(
                    message = "Načítám programy...",
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            is HomeScreenState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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
                EmptyState(
                    message = currentState.message,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            is HomeScreenState.Error -> {
                ErrorState(
                    message = currentState.message,
                    onRetry = { onRefresh() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}
