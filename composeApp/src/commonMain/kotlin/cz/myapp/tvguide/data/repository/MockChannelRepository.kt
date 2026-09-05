package cz.myapp.tvguide.data.repository

import cz.myapp.tvguide.data.mock.DelaySimulator
import cz.myapp.tvguide.data.mock.MockChannels
import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.ChannelCategory
import cz.myapp.tvguide.domain.repository.ChannelRepository
import cz.myapp.tvguide.util.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Mock implementation of ChannelRepository for Phase 1 prototype.
 * 
 * Uses in-memory mock data with simulated network delays.
 */
class MockChannelRepository : ChannelRepository {
    
    private val tag = "MockChannelRepository"
    
    override fun getAllChannels(): Flow<List<Channel>> = flow {
        AppLogger.d(tag) { "getAllChannels: Fetching all channels" }
        DelaySimulator.randomDelay()
        
        val channels = MockChannels.all.sortedBy { it.number }
        AppLogger.d(tag) { "getAllChannels: Returning ${channels.size} channels" }
        emit(channels)
    }
    
    override fun getChannelById(channelId: String): Flow<Channel?> = flow {
        AppLogger.d(tag) { "getChannelById: channelId=$channelId" }
        DelaySimulator.fastDelay()
        
        val channel = MockChannels.all.find { it.id == channelId }
        AppLogger.d(tag) { "getChannelById: Found channel: ${channel?.name}" }
        emit(channel)
    }
    
    override fun getChannelsByCategory(category: ChannelCategory): Flow<List<Channel>> = flow {
        AppLogger.d(tag) { "getChannelsByCategory: category=$category" }
        DelaySimulator.randomDelay()
        
        val channels = MockChannels.all
            .filter { it.category == category }
            .sortedBy { it.number }
        AppLogger.d(tag) { "getChannelsByCategory: Returning ${channels.size} channels" }
        emit(channels)
    }
    
    override fun searchChannels(query: String): Flow<List<Channel>> = flow {
        AppLogger.d(tag) { "searchChannels: query='$query'" }
        DelaySimulator.randomDelay()
        
        val normalizedQuery = query.lowercase().trim()
        val channels = if (normalizedQuery.isEmpty()) {
            MockChannels.all
        } else {
            MockChannels.all.filter { channel ->
                channel.name.lowercase().contains(normalizedQuery) ||
                channel.description.lowercase().contains(normalizedQuery)
            }
        }.sortedBy { it.number }
        
        AppLogger.d(tag) { "searchChannels: Returning ${channels.size} channels" }
        emit(channels)
    }
}
