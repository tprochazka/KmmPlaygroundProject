package cz.myapp.tvguide.presentation.screens.list

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.domain.usecase.GetChronologicalProgramsUseCase
import cz.myapp.tvguide.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlin.time.Duration.Companion.hours

/**
 * Screen model for ListScreen.
 * 
 * Manages state for chronological program list view:
 * - Loading programs sorted by time
 * - Channel filtering
 * - Infinite scroll pagination
 * - Pull-to-refresh support
 * 
 * US4: As a user, I want to browse all programs chronologically.
 */
class ListScreenModel(
    private val getChronologicalProgramsUseCase: GetChronologicalProgramsUseCase
) : ScreenModel {
    private val _state = MutableStateFlow<ListScreenState>(ListScreenState.Loading)
    val state: StateFlow<ListScreenState> = _state.asStateFlow()

    // Cache pro jednotlivé dny
    private val dayStates = mutableMapOf<LocalDate, MutableStateFlow<ListScreenState>>()

    private var currentChannelFilter: String? = null
    private var lastProgramTime: Instant? = null
    private var isLoadingMore = false

    // Nový stav pro vybraný den
    private var selectedDay: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    init {
        loadPrograms()
    }
    
    /**
     * Load programs for selected day.
     */
    fun loadPrograms() {
        loadProgramsForDay(selectedDay)
    }
    
    /**
     * Předčíst data pro konkrétní den
     */
    fun preloadDay(day: LocalDate) {
        if (!dayStates.containsKey(day)) {
            loadProgramsForDay(day)
        }
    }
    
    /**
     * Získat state pro konkrétní den
     */
    fun getStateForDay(day: LocalDate): StateFlow<ListScreenState> {
        // Pokud state pro den neexistuje, vytvoř ho a načti data
        if (!dayStates.containsKey(day)) {
            val newState = MutableStateFlow<ListScreenState>(ListScreenState.Loading)
            dayStates[day] = newState
            loadProgramsForDay(day)
        }
        
        return dayStates[day]!!
    }
    
    /**
     * Načíst programy pro konkrétní den
     */
    private fun loadProgramsForDay(day: LocalDate) {
        // Získat nebo vytvořit state pro den
        val dayState = dayStates.getOrPut(day) {
            MutableStateFlow(ListScreenState.Loading)
        }
        
        // Pokud už se načítá nebo je načteno, nepokračuj
        if (dayState.value is ListScreenState.Success || 
            dayState.value is ListScreenState.Empty) {
            return
        }
        
        screenModelScope.launch {
            dayState.value = ListScreenState.Loading

            try {
                AppLogger.d("ListScreenModel") { "Loading programs for $day..." }

                // Vypočítat časový rozsah pro vybraný den
                val startOfDay = LocalDateTime(day.year, day.month, day.dayOfMonth, 0, 0, 0)
                    .toInstant(TimeZone.currentSystemDefault())
                val endOfDay = startOfDay + 24.hours

                val programs = getChronologicalProgramsUseCase(
                    startTime = startOfDay,
                    endTime = endOfDay,
                    channelId = currentChannelFilter,
                    useFavorites = false
                )

                AppLogger.d("ListScreenModel") { "Loaded ${programs.size} programs for $day" }

                if (programs.isEmpty()) {
                    dayState.value = ListScreenState.Empty(message = "Žádné programy k zobrazení")
                } else {
                    dayState.value = ListScreenState.Success(
                        programs = programs,
                        selectedChannelId = currentChannelFilter,
                        hasMore = false // Pro jeden den není infinite scroll
                    )
                }
            } catch (e: Exception) {
                AppLogger.e("ListScreenModel", e) { "Error loading programs for $day" }
                dayState.value = ListScreenState.Error(
                    message = e.message ?: "Nepodařilo se načíst programy"
                )
            }
        }
        
        // Aktualizovat hlavní state pokud je to vybraný den
        if (day == selectedDay) {
            screenModelScope.launch {
                dayState.collect { newState ->
                    _state.value = newState
                }
            }
        }
    }
    /**
     * Změna vybraného dne a reload programů
     */
    fun setSelectedDay(day: LocalDate) {
        if (selectedDay != day) {
            selectedDay = day
            
            // Aktualizovat hlavní state
            val dayState = dayStates[day]
            if (dayState != null) {
                _state.value = dayState.value
            } else {
                loadPrograms()
            }
        }
    }
    
    /**
     * Vrací aktuálně vybraný den
     */
    fun getSelectedDay(): LocalDate = selectedDay    /**
     * Refresh programs (for pull-to-refresh).
     */
    fun refresh() {
        AppLogger.d("ListScreenModel") { "Manual refresh triggered" }
        loadPrograms()
    }
    
    /**
     * Load next page of programs for infinite scroll.
     */
    fun loadMore() {
        val currentLastTime = lastProgramTime ?: return
        if (isLoadingMore) return
        
        val currentState = _state.value
        if (currentState !is ListScreenState.Success) return
        
        isLoadingMore = true
        
        screenModelScope.launch {
            try {
                AppLogger.d("ListScreenModel") { "Loading more programs from ${currentLastTime}..." }
                
                val morePrograms = getChronologicalProgramsUseCase.getNextPage(
                    lastProgramTime = currentLastTime,
                    channelId = currentChannelFilter,
                    useFavorites = false
                )
                
                AppLogger.d("ListScreenModel") { "Loaded ${morePrograms.size} more programs" }
                
                if (morePrograms.isNotEmpty()) {
                    // Update last program time
                    lastProgramTime = morePrograms.lastOrNull()?.second?.endTime
                    
                    // Append to existing programs
                    _state.value = currentState.copy(
                        programs = currentState.programs + morePrograms,
                        hasMore = morePrograms.isNotEmpty()
                    )
                } else {
                    // No more programs available
                    _state.value = currentState.copy(hasMore = false)
                }
            } catch (e: Exception) {
                AppLogger.e("ListScreenModel", e) { "Error loading more programs" }
                // Keep current state, just log error
            } finally {
                isLoadingMore = false
            }
        }
    }
    
    /**
     * Filter programs by channel.
     * 
     * @param channelId Channel ID to filter by, or null for all channels
     */
    fun filterByChannel(channelId: String?) {
        if (currentChannelFilter == channelId) {
            // Same filter, no change needed
            return
        }
        
        AppLogger.d("ListScreenModel") { "Filtering by channel: $channelId" }
        currentChannelFilter = channelId
        loadPrograms()
    }
    
    /**
     * Clear channel filter (show all channels).
     */
    fun clearChannelFilter() {
        filterByChannel(null)
    }
}

/**
 * UI state for ListScreen.
 */
sealed class ListScreenState {
    /**
     * Loading state - initial load or refresh in progress.
     */
    data object Loading : ListScreenState()
    
    /**
     * Success state - programs loaded successfully.
     * 
     * @property programs List of (Channel, Program) pairs sorted by time
     * @property selectedChannelId Currently selected channel filter (null = all channels)
     * @property hasMore Whether more programs are available for infinite scroll
     */
    data class Success(
        val programs: List<Pair<Channel, Program>>,
        val selectedChannelId: String? = null,
        val hasMore: Boolean = true
    ) : ListScreenState()
    
    /**
     * Empty state - no programs found.
     * 
     * @property message Message to display to user
     */
    data class Empty(
        val message: String
    ) : ListScreenState()
    
    /**
     * Error state - failed to load programs.
     * 
     * @property message Error message to display
     */
    data class Error(
        val message: String
    ) : ListScreenState()
}
