package cz.myapp.tvguide.domain.usecase

import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.repository.ChannelRepository
import cz.myapp.tvguide.domain.repository.UserPreferencesRepository
import cz.myapp.tvguide.util.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine

/**
 * Use case to get user's favorite channels.
 * 
 * Returns channels from user's default favorite list, ordered by list position.
 * Falls back to all channels if no favorites configured.
 * 
 * US2: As a user, I want to manage favorite channels.
 */
class GetFavoriteChannelsUseCase(
    private val channelRepository: ChannelRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    /**
     * Get favorite channels for current user.
     * 
     * @param userId User ID (default "default_user" for Phase 1 prototype)
     * @return Flow of favorite channels, ordered by favorite list position.
     *         Returns all channels if no favorites configured.
     */
    operator fun invoke(userId: String = "default_user"): Flow<List<Channel>> {
        AppLogger.d("GetFavoriteChannelsUseCase") { "invoke called with userId=$userId" }
        return combine(
            channelRepository.getAllChannels(),
            userPreferencesRepository.getFavoriteChannelLists(userId)
        ) { allChannels, favoriteLists ->
            AppLogger.d("GetFavoriteChannelsUseCase") { "All channels: ${allChannels.size}, Lists: ${favoriteLists.size}" }
            
            // Find default favorite list
            val defaultList = favoriteLists.firstOrNull { it.isDefault }
            
            AppLogger.d("GetFavoriteChannelsUseCase") { "Default list: ${defaultList?.id}, channelIds: ${defaultList?.channelIds?.size}" }
            
            if (defaultList != null && defaultList.channelIds.isNotEmpty()) {
                // Create map for quick lookup
                val channelMap = allChannels.associateBy { it.id }
                
                // Return channels in favorite list order
                val favoriteChannels = defaultList.channelIds.mapNotNull { channelId ->
                    channelMap[channelId]
                }
                
                AppLogger.i("GetFavoriteChannelsUseCase") { "Returning ${favoriteChannels.size} favorite channels" }
                favoriteChannels
            } else {
                // Fallback: return all channels if no favorites configured
                AppLogger.i("GetFavoriteChannelsUseCase") { "No favorites configured, returning all ${allChannels.size} channels" }
                allChannels
            }
        }.catch { e ->
            AppLogger.e("GetFavoriteChannelsUseCase", e) { "Error getting favorite channels" }
            // Emit empty list on error
            emit(emptyList())
        }
    }
    
    /**
     * Get channels from a specific favorite list.
     * 
     * @param userId User ID
     * @param listId Favorite list ID
     * @return Flow of channels in the specified list
     */
    fun fromList(userId: String = "default_user", listId: String): Flow<List<Channel>> {
        return combine(
            channelRepository.getAllChannels(),
            userPreferencesRepository.getFavoriteChannelLists(userId)
        ) { allChannels, favoriteLists ->
            val list = favoriteLists.firstOrNull { it.id == listId }
            
            if (list != null) {
                val channelMap = allChannels.associateBy { it.id }
                list.channelIds.mapNotNull { channelId ->
                    channelMap[channelId]
                }
            } else {
                emptyList()
            }
        }.catch { e ->
            AppLogger.e("GetFavoriteChannelsUseCase", e) { "Error getting channels from list $listId" }
            emit(emptyList())
        }
    }
}
