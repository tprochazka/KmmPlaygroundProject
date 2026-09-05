package cz.myapp.tvguide.presentation.screens.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.domain.model.ProgramCast
import cz.myapp.tvguide.domain.repository.ChannelRepository
import cz.myapp.tvguide.domain.repository.ProgramRepository
import cz.myapp.tvguide.domain.repository.UserPreferencesRepository
import cz.myapp.tvguide.domain.usecase.GetProgramDetailsUseCase
import cz.myapp.tvguide.domain.usecase.GetSimilarProgramsUseCase
import cz.myapp.tvguide.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Screen model for DetailScreen.
 * 
 * Manages state for program detail view:
 * - Loading program details with cast/crew
 * - Loading similar programs with their channels
 * - Favorite program toggle (T130)
 * - Handling loading/error states
 * 
 * US5: As a user, I want to see detailed information about a program.
 */
class DetailScreenModel(
    private val programId: String,
    private val getProgramDetailsUseCase: GetProgramDetailsUseCase,
    private val getSimilarProgramsUseCase: GetSimilarProgramsUseCase,
    private val channelRepository: ChannelRepository,
    private val programRepository: ProgramRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ScreenModel {
    
    private val _state = MutableStateFlow<DetailScreenState>(DetailScreenState.Loading)
    val state: StateFlow<DetailScreenState> = _state.asStateFlow()
    
    // T130: Track if program is favorited
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    
    init {
        loadProgramDetails()
        loadFavoriteStatus()
    }
    
    /**
     * Load program details with cast and similar programs.
     */
    fun loadProgramDetails() {
        screenModelScope.launch {
            _state.value = DetailScreenState.Loading
            
            try {
                AppLogger.d("DetailScreenModel") { "Loading program details for: $programId" }
                
                // Get program details with cast
                val detailsResult = getProgramDetailsUseCase(programId)
                if (detailsResult == null) {
                    _state.value = DetailScreenState.Error("Program nenalezen")
                    return@launch
                }
                
                val (program, cast) = detailsResult
                
                AppLogger.d("DetailScreenModel") { "Loaded program: ${program.title}" }
                
                // Get similar programs
                val similarProgramsList = getSimilarProgramsUseCase(programId)
                
                AppLogger.d("DetailScreenModel") { "Loaded ${similarProgramsList.size} similar programs" }
                
                // Get channels for similar programs
                val allChannels = channelRepository.getAllChannels().first()
                val similarPrograms = similarProgramsList.mapNotNull { similarProgram ->
                    val channel = allChannels.find { it.id == similarProgram.channelId }
                    if (channel != null) {
                        channel to similarProgram
                    } else null
                }
                
                // Get broadcast schedule: Find programs with same title across all channels
                AppLogger.d("DetailScreenModel") { "Loading broadcast schedule for: ${program.title}" }
                val broadcastSchedule = programRepository.searchPrograms(
                    query = program.title
                ).first()
                    .filter { it.id != program.id } // Exclude the current program
                    .sortedBy { it.startTime }
                    .take(10) // Limit to 10 broadcasts
                
                val broadcastsWithChannels = broadcastSchedule.mapNotNull { broadcast ->
                    val channel = allChannels.find { it.id == broadcast.channelId }
                    if (channel != null) {
                        channel to broadcast
                    } else null
                }
                
                AppLogger.d("DetailScreenModel") { "Found ${broadcastsWithChannels.size} broadcast schedules" }
                
                _state.value = DetailScreenState.Success(
                    program = program,
                    cast = cast,
                    similarPrograms = similarPrograms,
                    broadcastSchedule = broadcastsWithChannels
                )
            } catch (e: Exception) {
                AppLogger.e("DetailScreenModel", e) { "Error loading program details" }
                _state.value = DetailScreenState.Error(
                    message = e.message ?: "Nepodařilo se načíst detail programu"
                )
            }
        }
    }
    
    /**
     * Load favorite status for the current program (T130).
     */
    private fun loadFavoriteStatus() {
        screenModelScope.launch {
            userPreferencesRepository.isProgramFavorite(programId).collect { favorite ->
                _isFavorite.value = favorite
                AppLogger.d("DetailScreenModel") { "Program $programId favorite status: $favorite" }
            }
        }
    }
    
    /**
     * Toggle favorite status of the current program (T130).
     */
    fun toggleFavorite() {
        screenModelScope.launch {
            try {
                val newStatus = userPreferencesRepository.toggleFavoriteProgram(programId)
                AppLogger.i("DetailScreenModel") { "Program $programId favorite toggled to: $newStatus" }
                // The flow will automatically update _isFavorite
            } catch (e: Exception) {
                AppLogger.e("DetailScreenModel", e) { "Error toggling favorite for program $programId" }
            }
        }
    }
    
    /**
     * Refresh program details (for pull-to-refresh).
     */
    fun refresh() {
        AppLogger.d("DetailScreenModel") { "Manual refresh triggered" }
        loadProgramDetails()
    }
}

/**
 * UI state for DetailScreen.
 */
sealed class DetailScreenState {
    /**
     * Loading state - initial load or refresh in progress.
     */
    data object Loading : DetailScreenState()
    
    /**
     * Success state - program details loaded successfully.
     * 
     * @property program The program details
     * @property cast Cast and crew information
     * @property similarPrograms List of similar programs with their channels
     * @property broadcastSchedule List of broadcast times across all channels
     */
    data class Success(
        val program: Program,
        val cast: ProgramCast,
        val similarPrograms: List<Pair<Channel, Program>>,
        val broadcastSchedule: List<Pair<Channel, Program>> = emptyList()
    ) : DetailScreenState()
    
    /**
     * Error state - failed to load program details.
     * 
     * @property message Error message to display
     */
    data class Error(
        val message: String
    ) : DetailScreenState()
}
