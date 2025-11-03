package cz.myapp.tvguide.presentation.screens.epg

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cz.myapp.tvguide.di.DomainModule
import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.domain.usecase.GetEpgDataUseCase
import cz.myapp.tvguide.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

/**
 * ScreenModel for EPG (Electronic Program Guide) grid view (US3).
 * 
 * Manages EPG data loading, time range selection, and grid state.
 */
class EpgScreenModel(
    private val getEpgDataUseCase: GetEpgDataUseCase = DomainModule.getEpgDataUseCase
) : ScreenModel {
    
    private val tag = "EpgScreenModel"
    
    private val _state = MutableStateFlow(EpgState())
    val state: StateFlow<EpgState> = _state.asStateFlow()
    
    init {
        loadEpgData()
    }
    
    /**
     * Load EPG data for the default time range (current time + 7 days).
     */
    fun loadEpgData() {
        AppLogger.d(tag) { "loadEpgData()" }
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val now = Clock.System.now()
                val endTime = now + 7.days
                
                val epgData = getEpgDataUseCase(
                    startTime = now,
                    endTime = endTime,
                    useFavorites = _state.value.showFavoritesOnly
                )
                
                AppLogger.i(tag) { "Loaded EPG data for ${epgData.size} channels" }
                
                _state.update { it.copy(
                    epgData = epgData,
                    currentTime = now,
                    startTime = now,
                    endTime = endTime,
                    isLoading = false
                ) }
            } catch (e: Exception) {
                AppLogger.e(tag, e) { "Failed to load EPG data" }
                _state.update { it.copy(
                    isLoading = false,
                    error = "Failed to load EPG data: ${e.message}"
                ) }
            }
        }
    }
    
    /**
     * Toggle between favorite channels and all channels.
     */
    fun toggleFavoritesFilter() {
        AppLogger.d(tag) { "toggleFavoritesFilter()" }
        _state.update { it.copy(showFavoritesOnly = !it.showFavoritesOnly) }
        loadEpgData()
    }
    
    /**
     * Select a program to show details.
     */
    fun selectProgram(program: Program?) {
        AppLogger.d(tag) { "selectProgram: ${program?.title}" }
        _state.update { it.copy(selectedProgram = program) }
    }
    
    /**
     * Adjust the time range zoom level.
     * @param hoursVisible Number of hours visible in the grid (3, 6, 12, 24)
     */
    fun adjustTimeZoom(hoursVisible: Int) {
        AppLogger.d(tag) { "adjustTimeZoom: $hoursVisible hours" }
        _state.update { it.copy(hoursVisible = hoursVisible) }
    }
    
    /**
     * Scroll to a specific time.
     */
    fun scrollToTime(time: Instant) {
        AppLogger.d(tag) { "scrollToTime: $time" }
        // This will be handled by the UI layer with scroll state
        _state.update { it.copy(currentTime = time) }
    }
    
    /**
     * Clear error message.
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

/**
 * UI state for EPG screen.
 */
data class EpgState(
    val epgData: Map<Channel, List<Program>> = emptyMap(),
    val currentTime: Instant = Clock.System.now(),
    val startTime: Instant = Clock.System.now(),
    val endTime: Instant = Clock.System.now() + 7.days,
    val hoursVisible: Int = 6, // Number of hours visible in the grid at once
    val showFavoritesOnly: Boolean = true,
    val selectedProgram: Program? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    /**
     * Get channels sorted by number.
     */
    val channels: List<Channel>
        get() = epgData.keys.sortedBy { it.number }
    
    /**
     * Get all time slots for the visible time range.
     * Creates hour markers for the horizontal axis.
     */
    fun getTimeSlots(): List<Instant> {
        val slots = mutableListOf<Instant>()
        var current = startTime
        while (current < endTime) {
            slots.add(current)
            current += 1.hours
        }
        return slots
    }
}
