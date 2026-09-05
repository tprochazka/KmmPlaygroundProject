package cz.myapp.tvguide.presentation.screens.favorites

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cz.myapp.tvguide.di.DomainModule
import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.ChannelCategory
import cz.myapp.tvguide.domain.model.FavoriteChannelList
import cz.myapp.tvguide.domain.usecase.CreateChannelListUseCase
import cz.myapp.tvguide.domain.usecase.GetAllChannelsUseCase
import cz.myapp.tvguide.domain.usecase.UpdateFavoriteChannelsUseCase
import cz.myapp.tvguide.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ScreenModel for Favorites screen (US2).
 * 
 * Manages channel selection, ordering, custom lists, and search/filter.
 */
class FavoritesScreenModel(
    private val getAllChannelsUseCase: GetAllChannelsUseCase = DomainModule.getAllChannelsUseCase,
    private val updateFavoriteChannelsUseCase: UpdateFavoriteChannelsUseCase = DomainModule.updateFavoriteChannelsUseCase,
    private val createChannelListUseCase: CreateChannelListUseCase = DomainModule.createChannelListUseCase
) : ScreenModel {
    
    private val tag = "FavoritesScreenModel"
    
    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()
    
    init {
        loadChannels()
    }
    
    /**
     * Load all available channels.
     */
    private fun loadChannels() {
        AppLogger.d(tag) { "loadChannels()" }
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val channels = getAllChannelsUseCase()
                AppLogger.i(tag) { "Loaded ${channels.size} channels" }
                
                _state.update { it.copy(
                    allChannels = channels,
                    filteredChannels = filterChannels(channels, it.searchQuery, it.selectedCategory),
                    isLoading = false
                ) }
            } catch (e: Exception) {
                AppLogger.e(tag, e) { "Failed to load channels" }
                _state.update { it.copy(
                    isLoading = false,
                    error = "Failed to load channels: ${e.message}"
                ) }
            }
        }
    }
    
    /**
     * Toggle channel favorite status.
     */
    fun toggleFavorite(channelId: String) {
        AppLogger.d(tag) { "toggleFavorite: $channelId" }
        _state.update { state ->
            val newFavorites = if (channelId in state.favoriteChannelIds) {
                state.favoriteChannelIds - channelId
            } else {
                state.favoriteChannelIds + channelId
            }
            state.copy(favoriteChannelIds = newFavorites)
        }
        saveFavorites()
    }
    
    /**
     * Reorder favorite channels.
     */
    fun reorderFavorites(from: Int, to: Int) {
        AppLogger.d(tag) { "reorderFavorites: from=$from, to=$to" }
        _state.update { state ->
            val newList = state.favoriteChannelIds.toMutableList().apply {
                add(to, removeAt(from))
            }
            state.copy(favoriteChannelIds = newList)
        }
        saveFavorites()
    }
    
    /**
     * Save favorites to repository.
     */
    private fun saveFavorites() {
        screenModelScope.launch {
            try {
                updateFavoriteChannelsUseCase(_state.value.favoriteChannelIds)
                AppLogger.i(tag) { "Favorites saved: ${_state.value.favoriteChannelIds.size} channels" }
            } catch (e: Exception) {
                AppLogger.e(tag, e) { "Failed to save favorites" }
                _state.update { it.copy(error = "Failed to save favorites: ${e.message}") }
            }
        }
    }
    
    /**
     * Update search query and filter channels.
     */
    fun updateSearchQuery(query: String) {
        AppLogger.d(tag) { "updateSearchQuery: $query" }
        _state.update { state ->
            state.copy(
                searchQuery = query,
                filteredChannels = filterChannels(state.allChannels, query, state.selectedCategory)
            )
        }
    }
    
    /**
     * Select a category filter.
     */
    fun selectCategory(category: ChannelCategory?) {
        AppLogger.d(tag) { "selectCategory: $category" }
        _state.update { state ->
            state.copy(
                selectedCategory = category,
                filteredChannels = filterChannels(state.allChannels, state.searchQuery, category)
            )
        }
    }
    
    /**
     * Filter channels by search query and category.
     */
    private fun filterChannels(
        channels: List<Channel>,
        query: String,
        category: ChannelCategory?
    ): List<Channel> {
        var filtered = channels
        
        // Filter by category
        if (category != null) {
            filtered = filtered.filter { it.category == category }
        }
        
        // Filter by search query
        if (query.isNotBlank()) {
            val lowerQuery = query.lowercase()
            filtered = filtered.filter { channel ->
                channel.name.lowercase().contains(lowerQuery) ||
                channel.number.toString().contains(lowerQuery)
            }
        }
        
        return filtered
    }
    
    /**
     * Create a new custom channel list.
     */
    fun createCustomList(name: String, channelIds: List<String>) {
        AppLogger.d(tag) { "createCustomList: name=$name, channels=${channelIds.size}" }
        screenModelScope.launch {
            try {
                val listId = createChannelListUseCase(name, channelIds)
                AppLogger.i(tag) { "Custom list created: $listId" }
                _state.update { it.copy(showCreateListDialog = false) }
            } catch (e: Exception) {
                AppLogger.e(tag, e) { "Failed to create custom list" }
                _state.update { it.copy(error = "Failed to create list: ${e.message}") }
            }
        }
    }
    
    /**
     * Show/hide create list dialog.
     */
    fun setShowCreateListDialog(show: Boolean) {
        _state.update { it.copy(showCreateListDialog = show) }
    }
    
    /**
     * Clear error message.
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

/**
 * UI state for Favorites screen.
 */
data class FavoritesState(
    val allChannels: List<Channel> = emptyList(),
    val filteredChannels: List<Channel> = emptyList(),
    val favoriteChannelIds: List<String> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: ChannelCategory? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCreateListDialog: Boolean = false
)
