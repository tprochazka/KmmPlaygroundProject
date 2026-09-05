package cz.myapp.tvguide.domain.usecase

import cz.myapp.tvguide.domain.repository.UserPreferencesRepository

/**
 * Use case for updating the user's favorite channels list.
 * 
 * Handles adding/removing channels from favorites and reordering.
 */
class UpdateFavoriteChannelsUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    /**
     * Updates the favorite channels list with new order and selection.
     * 
     * @param channelIds List of channel IDs in the desired order
     */
    suspend operator fun invoke(channelIds: List<String>) {
        userPreferencesRepository.updateFavoriteChannels(channelIds)
    }
}
