package cz.myapp.tvguide.domain.usecase

import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.domain.repository.ChannelRepository
import cz.myapp.tvguide.domain.repository.ProgramRepository
import cz.myapp.tvguide.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

/**
 * Use case to retrieve programs sorted chronologically for list view.
 * Supports filtering by channel and pagination for infinite scroll.
 */
class GetChronologicalProgramsUseCase(
    private val programRepository: ProgramRepository,
    private val channelRepository: ChannelRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    /**
     * Get programs sorted chronologically for specified time range.
     * 
     * @param startTime Start of time range (defaults to now)
     * @param endTime End of time range (defaults to 7 days from start)
     * @param channelId Optional channel ID to filter by
     * @param useFavorites If true, only shows programs from favorite channels
     * @return List of programs sorted by start time
     */
    suspend operator fun invoke(
        startTime: Instant? = null,
        endTime: Instant? = null,
        channelId: String? = null,
        useFavorites: Boolean = false
    ): List<Pair<Channel, Program>> {
        val actualStartTime = startTime ?: Clock.System.now()
        val actualEndTime = endTime ?: (actualStartTime + 7.days)
        
        // Get channels to include
        val channels = when {
            channelId != null -> {
                // Single channel filter
                val channel = channelRepository.getAllChannels().first()
                    .find { it.id == channelId }
                listOfNotNull(channel)
            }
            useFavorites -> {
                // Favorite channels only
                val favoriteChannelLists = userPreferencesRepository.getFavoriteChannelLists()
                    .first()
                val favoriteIds = favoriteChannelLists
                    .firstOrNull()
                    ?.channelIds ?: emptyList()
                
                channelRepository.getAllChannels().first()
                    .filter { it.id in favoriteIds }
            }
            else -> {
                // All channels
                channelRepository.getAllChannels().first()
            }
        }
        
        // Get programs for each channel
        val allPrograms = mutableListOf<Pair<Channel, Program>>()
        
        channels.forEach { channel ->
            val programs = programRepository.getProgramsByTimeRange(
                channelId = channel.id,
                startTime = actualStartTime,
                endTime = actualEndTime
            ).first()
            
            programs.forEach { program ->
                allPrograms.add(channel to program)
            }
        }
        
        // Sort by start time
        return allPrograms.sortedBy { (_, program) -> program.startTime }
    }
    
    /**
     * Get next page of programs for infinite scroll.
     * 
     * @param lastProgramTime The end time of the last program shown
     * @param channelId Optional channel ID to filter by
     * @param useFavorites If true, only shows programs from favorite channels
     * @param pageSize Number of programs to fetch (default 50)
     * @return List of next programs sorted by start time
     */
    suspend fun getNextPage(
        lastProgramTime: Instant,
        channelId: String? = null,
        useFavorites: Boolean = false,
        pageSize: Int = 50
    ): List<Pair<Channel, Program>> {
        // Fetch programs for next day
        val actualStartTime = lastProgramTime
        val actualEndTime = actualStartTime + 1.days
        
        return invoke(
            startTime = actualStartTime,
            endTime = actualEndTime,
            channelId = channelId,
            useFavorites = useFavorites
        ).take(pageSize)
    }
}
