package cz.myapp.tvguide.domain.usecase

import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.repository.ChannelRepository
import kotlinx.coroutines.flow.first

/**
 * Use case for retrieving all available channels.
 * 
 * Used by Favorites screen to display the complete channel list
 * for selection and management.
 */
class GetAllChannelsUseCase(
    private val channelRepository: ChannelRepository
) {
    /**
     * Retrieves all channels sorted by number.
     * 
     * @return List of all channels
     */
    suspend operator fun invoke(): List<Channel> {
        return channelRepository.getAllChannels().first()
    }
}
