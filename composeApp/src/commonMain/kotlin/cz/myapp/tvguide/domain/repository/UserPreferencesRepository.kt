package cz.myapp.tvguide.domain.repository

import cz.myapp.tvguide.domain.model.FavoriteChannelList
import cz.myapp.tvguide.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Repository for user preferences and favorites.
 * 
 * Provides access to user settings and favorite channel lists.
 */
interface UserPreferencesRepository {
    
    /**
     * Get user preferences.
     * 
     * @param userId The user ID (defaults to "default" for single-user app)
     * @return Flow of user preferences
     */
    fun getUserPreferences(userId: String = "default"): Flow<UserPreferences>
    
    /**
     * Update user preferences.
     * 
     * @param preferences The updated preferences
     */
    suspend fun updateUserPreferences(preferences: UserPreferences)
    
    /**
     * Get all favorite channel lists for a user.
     * 
     * @param userId The user ID
     * @return Flow of favorite channel lists
     */
    fun getFavoriteChannelLists(userId: String = "default"): Flow<List<FavoriteChannelList>>
    
    /**
     * Get a single favorite channel list by ID.
     * 
     * @param listId The list ID
     * @return Flow of the list, or null if not found
     */
    fun getFavoriteChannelListById(listId: String): Flow<FavoriteChannelList?>
    
    /**
     * Get the default favorite channel list.
     * 
     * @param userId The user ID
     * @return Flow of the default list, or null if none set
     */
    fun getDefaultFavoriteChannelList(userId: String = "default"): Flow<FavoriteChannelList?>
    
    /**
     * Create a new favorite channel list.
     * 
     * @param list The list to create
     * @return The created list ID
     */
    suspend fun createFavoriteChannelList(list: FavoriteChannelList): String
    
    /**
     * Update an existing favorite channel list.
     * 
     * @param list The list to update
     */
    suspend fun updateFavoriteChannelList(list: FavoriteChannelList)
    
    /**
     * Delete a favorite channel list.
     * 
     * @param listId The list ID to delete
     */
    suspend fun deleteFavoriteChannelList(listId: String)
    
    /**
     * Update favorite channels in the default list.
     * Convenience method for simple favorite management.
     * 
     * @param channelIds List of channel IDs in desired order
     */
    suspend fun updateFavoriteChannels(channelIds: List<String>)
    
    /**
     * Create a new channel list (alias for createFavoriteChannelList).
     * 
     * @param list The list to create
     */
    suspend fun createChannelList(list: FavoriteChannelList) {
        createFavoriteChannelList(list)
    }
}
