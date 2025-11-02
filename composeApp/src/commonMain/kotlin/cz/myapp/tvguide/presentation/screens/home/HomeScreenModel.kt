package cz.myapp.tvguide.presentation.screens.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.domain.usecase.GetCurrentProgramsUseCase
import cz.myapp.tvguide.domain.usecase.GetFavoriteChannelsUseCase
import cz.myapp.tvguide.util.AppLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Screen model for HomeScreen.
 * 
 * Manages state for current TV programs view:
 * - Loading current programs for favorite channels
 * - Handling loading/error states
 * - Auto-refresh when programs end
 * - Pull-to-refresh support
 * 
 * US1: As a user, I want to see what's currently on TV.
 */
class HomeScreenModel(
    private val getCurrentProgramsUseCase: GetCurrentProgramsUseCase,
    private val getFavoriteChannelsUseCase: GetFavoriteChannelsUseCase
) : ScreenModel {
    
    private val _state = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()
    
    private var autoRefreshJob: Job? = null
    
    init {
        loadCurrentPrograms()
        startAutoRefresh()
    }
    
    /**
     * Load current programs for favorite channels.
     */
    fun loadCurrentPrograms() {
        screenModelScope.launch {
            _state.value = HomeScreenState.Loading
            
            try {
                // Get favorite channels first
                val favoriteChannels = getFavoriteChannelsUseCase()
                    .first() // Take first emission
                
                if (favoriteChannels.isEmpty()) {
                    _state.value = HomeScreenState.Empty(message = "Žádné oblíbené kanály")
                    return@launch
                }
                
                // Get current programs for those channels
                getCurrentProgramsUseCase
                    .forChannels(favoriteChannels.map { it.id })
                    .collect { result ->
                        result.fold(
                            onSuccess = { programs ->
                                AppLogger.d("HomeScreenModel") { "Loaded ${programs.size} current programs" }
                                _state.value = HomeScreenState.Success(programs = programs)
                            },
                            onFailure = { error ->
                                AppLogger.e("HomeScreenModel", error) { "Error loading programs" }
                                _state.value = HomeScreenState.Error(
                                    message = error.message ?: "Nepodařilo se načíst programy"
                                )
                            }
                        )
                    }
            } catch (e: Exception) {
                AppLogger.e("HomeScreenModel", e) { "Error in loadCurrentPrograms" }
                _state.value = HomeScreenState.Error(
                    message = e.message ?: "Nepodařilo se načíst programy"
                )
            }
        }
    }
    
    /**
     * Refresh current programs (for pull-to-refresh).
     */
    fun refresh() {
        AppLogger.d("HomeScreenModel") { "Manual refresh triggered" }
        loadCurrentPrograms()
    }
    
    /**
     * Start auto-refresh timer.
     * Refreshes data every minute to ensure current programs are up-to-date.
     */
    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = screenModelScope.launch {
            while (true) {
                delay(60_000) // 1 minute
                AppLogger.d("HomeScreenModel") { "Auto-refresh triggered" }
                loadCurrentPrograms()
            }
        }
    }
    
    override fun onDispose() {
        super.onDispose()
        autoRefreshJob?.cancel()
    }
}

/**
 * UI state for HomeScreen.
 */
sealed class HomeScreenState {
    /**
     * Loading state - initial load or refresh in progress.
     */
    data object Loading : HomeScreenState()
    
    /**
     * Success state - programs loaded successfully.
     * 
     * @property programs List of (Channel, Program?) pairs showing current programs.
     *                    Program is null if channel has no current program.
     */
    data class Success(
        val programs: List<Pair<Channel, Program?>>
    ) : HomeScreenState()
    
    /**
     * Empty state - no favorite channels configured.
     * 
     * @property message Message to display to user
     */
    data class Empty(
        val message: String
    ) : HomeScreenState()
    
    /**
     * Error state - failed to load programs.
     * 
     * @property message Error message to display
     */
    data class Error(
        val message: String
    ) : HomeScreenState()
}
