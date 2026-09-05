package cz.myapp.tvguide.data.repository

import cz.myapp.tvguide.data.mock.DelaySimulator
import cz.myapp.tvguide.data.mock.mockFavoriteChannelList
import cz.myapp.tvguide.domain.model.FavoriteChannelList
import cz.myapp.tvguide.domain.model.UserPreferences
import cz.myapp.tvguide.domain.repository.UserPreferencesRepository
import cz.myapp.tvguide.presentation.theme.ThemeMode
import cz.myapp.tvguide.util.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

/**
 * Local in-memory implementation of UserPreferencesRepository for Phase 1 prototype.
 * 
 * Stores user preferences and favorite channel lists in memory.
 * In Phase 2, this will be replaced with Room database implementation.
 */
class LocalUserPreferencesRepository : UserPreferencesRepository {
    
    private val tag = "LocalUserPreferencesRepository"
    
    // In-memory storage
    private val preferencesFlow = MutableStateFlow(UserPreferences())
    private val favoriteListsFlow = MutableStateFlow<Map<String, FavoriteChannelList>>(
        mapOf(
            "default" to createDefaultFavoriteList()
        )
    )
    
    // T130: In-memory storage for favorite programs (Phase 1 prototype)
    private val favoriteProgramsFlow = MutableStateFlow<Map<String, Set<String>>>(
        emptyMap() // Map of userId to Set of programIds
    )
    
    override fun getUserPreferences(userId: String): Flow<UserPreferences> {
        AppLogger.d(tag) { "getUserPreferences: userId=$userId" }
        return preferencesFlow
    }
    
    override suspend fun updateUserPreferences(preferences: UserPreferences) {
        AppLogger.d(tag) { "updateUserPreferences: $preferences" }
        DelaySimulator.fastDelay()
        preferencesFlow.value = preferences
        AppLogger.i(tag) { "User preferences updated" }
    }
    
    override fun getFavoriteChannelLists(userId: String): Flow<List<FavoriteChannelList>> {
        AppLogger.d(tag) { "getFavoriteChannelLists: userId=$userId" }
        return favoriteListsFlow.map { map ->
            AppLogger.d(tag) { "favoriteListsFlow has ${map.size} entries: ${map.keys}" }
            val filtered = map.values.filter { it.userId == userId }
            AppLogger.d(tag) { "Filtered to ${filtered.size} lists for userId=$userId" }
            filtered.forEach { 
                AppLogger.d(tag) { "  - List: id=${it.id}, name=${it.name}, userId=${it.userId}, isDefault=${it.isDefault}, channels=${it.channelIds.size}" }
            }
            filtered
        }
    }
    
    override fun getFavoriteChannelListById(listId: String): Flow<FavoriteChannelList?> {
        AppLogger.d(tag) { "getFavoriteChannelListById: listId=$listId" }
        return favoriteListsFlow.map { it[listId] }
    }
    
    override fun getDefaultFavoriteChannelList(userId: String): Flow<FavoriteChannelList?> {
        AppLogger.d(tag) { "getDefaultFavoriteChannelList: userId=$userId" }
        return favoriteListsFlow.map { map ->
            map.values.find { it.userId == userId && it.isDefault }
        }
    }
    
    override suspend fun createFavoriteChannelList(list: FavoriteChannelList): String {
        AppLogger.d(tag) { "createFavoriteChannelList: name=${list.name}" }
        DelaySimulator.fastDelay()
        
        favoriteListsFlow.value = favoriteListsFlow.value + (list.id to list)
        AppLogger.i(tag) { "Favorite channel list created: ${list.id}" }
        return list.id
    }
    
    override suspend fun updateFavoriteChannelList(list: FavoriteChannelList) {
        AppLogger.d(tag) { "updateFavoriteChannelList: id=${list.id}, name=${list.name}" }
        DelaySimulator.fastDelay()
        
        favoriteListsFlow.value = favoriteListsFlow.value + (list.id to list)
        AppLogger.i(tag) { "Favorite channel list updated: ${list.id}" }
    }
    
    override suspend fun deleteFavoriteChannelList(listId: String) {
        AppLogger.d(tag) { "deleteFavoriteChannelList: listId=$listId" }
        DelaySimulator.fastDelay()
        
        favoriteListsFlow.value = favoriteListsFlow.value - listId
        AppLogger.i(tag) { "Favorite channel list deleted: $listId" }
    }
    
    override suspend fun updateFavoriteChannels(channelIds: List<String>) {
        AppLogger.d(tag) { "updateFavoriteChannels: channelIds=${channelIds.size}" }
        DelaySimulator.fastDelay()
        
        // Update the default list with new channel IDs
        val defaultList = favoriteListsFlow.value["default"]
        if (defaultList != null) {
            val updatedList = defaultList.copy(
                channelIds = channelIds,
                updatedAt = Clock.System.now()
            )
            favoriteListsFlow.value = favoriteListsFlow.value + ("default" to updatedList)
            AppLogger.i(tag) { "Default favorite channels updated: ${channelIds.size} channels" }
        } else {
            AppLogger.w(tag) { "Default favorite list not found, cannot update channels" }
        }
    }
    
    /**
     * Create a default favorite channel list with popular Czech channels.
     */
    private fun createDefaultFavoriteList(): FavoriteChannelList {
    val now = Clock.System.now()
        return mockFavoriteChannelList {
            id = "default"
            name = "Moje oblíbené"
            userId = "default_user"  // Match the default user ID used in use cases
            isDefault = true
            channelIds = listOf(
                "ct1", "ct2", "ct24", "ct_sport",
                "nova", "nova_cinema", "nova_sport1",
                "prima", "prima_cool", "prima_love",
                "filmbox", "eurosport1", "natgeo"
            )
            createdAt = now
            updatedAt = now
        }
    }
    
    // T130: Favorite Programs implementation (Phase 1 prototype)
    
    override fun getFavoritePrograms(userId: String): Flow<Set<String>> {
        AppLogger.d(tag) { "getFavoritePrograms: userId=$userId" }
        return favoriteProgramsFlow.map { it[userId] ?: emptySet() }
    }
    
    override fun isProgramFavorite(programId: String, userId: String): Flow<Boolean> {
        return favoriteProgramsFlow.map { map ->
            map[userId]?.contains(programId) == true
        }
    }
    
    override suspend fun addFavoriteProgram(programId: String, userId: String) {
        AppLogger.d(tag) { "addFavoriteProgram: programId=$programId, userId=$userId" }
        DelaySimulator.fastDelay()
        
        val currentFavorites = favoriteProgramsFlow.value[userId] ?: emptySet()
        val updatedFavorites = currentFavorites + programId
        
        favoriteProgramsFlow.value = favoriteProgramsFlow.value + (userId to updatedFavorites)
        AppLogger.i(tag) { "Program $programId added to favorites for user $userId" }
    }
    
    override suspend fun removeFavoriteProgram(programId: String, userId: String) {
        AppLogger.d(tag) { "removeFavoriteProgram: programId=$programId, userId=$userId" }
        DelaySimulator.fastDelay()
        
        val currentFavorites = favoriteProgramsFlow.value[userId] ?: emptySet()
        val updatedFavorites = currentFavorites - programId
        
        favoriteProgramsFlow.value = favoriteProgramsFlow.value + (userId to updatedFavorites)
        AppLogger.i(tag) { "Program $programId removed from favorites for user $userId" }
    }
    
    override suspend fun toggleFavoriteProgram(programId: String, userId: String): Boolean {
        AppLogger.d(tag) { "toggleFavoriteProgram: programId=$programId, userId=$userId" }
        DelaySimulator.fastDelay()
        
        val currentFavorites = favoriteProgramsFlow.value[userId] ?: emptySet()
        val isFavorite = currentFavorites.contains(programId)
        
        val updatedFavorites = if (isFavorite) {
            currentFavorites - programId
        } else {
            currentFavorites + programId
        }
        
        favoriteProgramsFlow.value = favoriteProgramsFlow.value + (userId to updatedFavorites)
        
        val newStatus = !isFavorite
        AppLogger.i(tag) { "Program $programId favorite toggled to $newStatus for user $userId" }
        return newStatus
    }
}
