package cz.myapp.tvguide.domain.repository

import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.ChannelCategory
import kotlinx.coroutines.flow.Flow

/**
 * Repository for channel data.
 * 
 * Provides access to TV channel information.
 */
interface ChannelRepository {
    
    /**
     * Get all available channels.
     * 
     * @return Flow of all channels, ordered by channel number
     */
    fun getAllChannels(): Flow<List<Channel>>
    
    /**
     * Get a single channel by ID.
     * 
     * @param channelId The channel ID
     * @return Flow of the channel, or null if not found
     */
    fun getChannelById(channelId: String): Flow<Channel?>
    
    /**
     * Get channels by category.
     * 
     * @param category The channel category to filter by
     * @return Flow of channels in the specified category
     */
    fun getChannelsByCategory(category: ChannelCategory): Flow<List<Channel>>
    
    /**
     * Search channels by name.
     * 
     * @param query Search query (case-insensitive)
     * @return Flow of channels matching the query
     */
    fun searchChannels(query: String): Flow<List<Channel>>
}
