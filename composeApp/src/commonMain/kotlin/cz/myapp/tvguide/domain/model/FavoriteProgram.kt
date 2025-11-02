package cz.myapp.tvguide.domain.model

import kotlinx.datetime.Instant

/**
 * Represents a program favorited by the user.
 * 
 * Users can favorite programs to receive notifications before they air.
 * 
 * @property id Unique identifier
 * @property programId Foreign key to Program
 * @property userId User identifier (for multi-user support in future)
 * @property addedAt When the program was favorited
 * @property notificationEnabled Whether to send notification before program starts
 * @property notificationMinutesBefore Minutes before program start to send notification
 */
data class FavoriteProgram(
    val id: String,
    val programId: String,
    val userId: String = "default",
    val addedAt: Instant,
    val notificationEnabled: Boolean = true,
    val notificationMinutesBefore: Int = 15
) {
    init {
        require(id.isNotBlank()) { "Favorite ID must not be blank" }
        require(programId.isNotBlank()) { "Program ID must not be blank" }
        require(userId.isNotBlank()) { "User ID must not be blank" }
        require(notificationMinutesBefore > 0) { 
            "Notification time must be positive" 
        }
    }
}
