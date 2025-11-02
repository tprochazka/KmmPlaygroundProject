package cz.myapp.tvguide.domain.usecase

import cz.myapp.tvguide.domain.model.FavoriteChannelList
import cz.myapp.tvguide.domain.repository.UserPreferencesRepository
import kotlin.time.Clock

/**
 * Use case for creating custom channel lists (Sports, Movies, Kids, etc.).
 * 
 * Allows users to organize channels into multiple lists for quick access.
 */
class CreateChannelListUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    /**
     * Creates a new custom channel list.
     * 
     * @param name Display name for the list (e.g., "Sports", "Movies")
     * @param channelIds Channel IDs to include in this list
     * @return ID of the newly created list
     */
    suspend operator fun invoke(name: String, channelIds: List<String>): String {
        val now = Clock.System.now()
        val newList = FavoriteChannelList(
            id = generateListId(),
            name = name,
            channelIds = channelIds,
            isDefault = false,
            createdAt = now,
            updatedAt = now
        )
        userPreferencesRepository.createChannelList(newList)
        return newList.id
    }
    
    private fun generateListId(): String {
        return "list_${System.currentTimeMillis()}"
    }
}
