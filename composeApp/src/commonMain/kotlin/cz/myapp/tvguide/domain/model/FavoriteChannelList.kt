package cz.myapp.tvguide.domain.model

import kotlin.time.Instant

/**
 * Represents a user-created list of favorite channels.
 * 
 * Users can create multiple channel lists (e.g., "Sports", "Movies", "All Channels").
 * 
 * @property id Unique identifier
 * @property name List name (e.g., "Sports Channels", "My Favorites")
 * @property channelIds Ordered list of channel IDs in this list
 * @property isDefault Whether this is the default list shown on app launch
 * @property userId User identifier (for multi-user support in future)
 * @property createdAt When the list was created
 * @property updatedAt Last update timestamp
 */
data class FavoriteChannelList(
    val id: String,
    val name: String,
    val channelIds: List<String>,
    val isDefault: Boolean = false,
    val userId: String = "default",
    val createdAt: Instant,
    val updatedAt: Instant
) {
    init {
        require(id.isNotBlank()) { "List ID must not be blank" }
        require(name.isNotBlank()) { "List name must not be blank" }
        require(name.length <= 100) { "List name must be max 100 characters" }
        require(userId.isNotBlank()) { "User ID must not be blank" }
    }
    
    /**
     * Number of channels in this list.
     */
    val channelCount: Int
        get() = channelIds.size
}
