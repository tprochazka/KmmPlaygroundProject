package cz.myapp.tvguide.domain.usecase

import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.domain.repository.ChannelRepository
import cz.myapp.tvguide.domain.repository.ProgramRepository
import cz.myapp.tvguide.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

/**
 * Use case for retrieving EPG (Electronic Program Guide) data.
 * 
 * Returns programs organized by channel for a specified time range.
 * Used by EPG grid view to display traditional time-channel grid.
 */
class GetEpgDataUseCase(
    private val channelRepository: ChannelRepository,
    private val programRepository: ProgramRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    /**
     * Get EPG data for favorite channels within a time range.
     * 
     * @param startTime Start of the time range
     * @param endTime End of the time range
     * @param useFavorites If true, only return favorite channels; if false, return all channels
     * @return Map of channel ID to list of programs in chronological order
     */
    suspend operator fun invoke(
        startTime: Instant,
        endTime: Instant,
        useFavorites: Boolean = true
    ): Map<Channel, List<Program>> {
        // Get channels (favorites or all)
        val channels = if (useFavorites) {
            val favoriteChannelLists = userPreferencesRepository.getFavoriteChannelLists().first()
            val defaultList = favoriteChannelLists.firstOrNull { it.isDefault }
            val favoriteIds = defaultList?.channelIds ?: emptyList()
            
            if (favoriteIds.isEmpty()) {
                // If no favorites, use first 10 channels
                channelRepository.getAllChannels().first().take(10)
            } else {
                val allChannels = channelRepository.getAllChannels().first()
                favoriteIds.mapNotNull { id ->
                    allChannels.find { it.id == id }
                }
            }
        } else {
            channelRepository.getAllChannels().first()
        }
        
        // Get programs for each channel in the time range
        val result = mutableMapOf<Channel, List<Program>>()
        
        for (channel in channels) {
            val programs = programRepository
                .getProgramsForChannel(channel.id, startTime, endTime)
                .first()
                .sortedBy { it.startTime }
            
            result[channel] = programs
        }
        
        return result
    }
    
    /**
     * Get EPG data for the next 7 days starting from now.
     */
    suspend fun getWeeklyEpgData(useFavorites: Boolean = true): Map<Channel, List<Program>> {
        val now = kotlin.time.Clock.System.now()
        val endTime = now + 7.days
        return invoke(now, endTime, useFavorites)
    }
}
