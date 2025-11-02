package cz.myapp.tvguide.domain.model

import kotlin.time.Instant

/**
 * Represents a TV channel in the program guide.
 * 
 * @property id Unique identifier (e.g., "ct1", "nova")
 * @property name Display name (e.g., "ČT1", "Nova")
 * @property logoUrl Channel logo image URL
 * @property category Channel category
 * @property number Channel number for sorting (must be positive and unique)
 * @property description Short description of the channel
 * @property languageCode ISO 639-1 language code (e.g., "cs", "sk")
 * @property isHd Whether the channel broadcasts in HD quality
 * @property isActive Whether the channel is currently broadcasting
 * @property streamUrl Optional live stream URL (for future use)
 * @property createdAt Record creation timestamp
 * @property updatedAt Last update timestamp
 */
data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String,
    val category: ChannelCategory,
    val number: Int,
    val description: String,
    val languageCode: String,
    val isHd: Boolean,
    val isActive: Boolean,
    val streamUrl: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    init {
        require(id.isNotBlank() && id.length <= 50) { 
            "Channel ID must be non-empty and max 50 characters" 
        }
        require(name.isNotBlank() && name.length <= 100) { 
            "Channel name must be non-empty and max 100 characters" 
        }
        require(number > 0) { 
            "Channel number must be positive" 
        }
        require(languageCode.length == 2) { 
            "Language code must be ISO 639-1 format (2 characters)" 
        }
    }
}

/**
 * Categories for organizing TV channels.
 */
enum class ChannelCategory {
    /** National public channels (ČT1, ČT2, etc.) */
    NATIONAL,
    
    /** Private channels (Nova, Prima, etc.) */
    PRIVATE,
    
    /** Regional channels */
    REGIONAL,
    
    /** Movie channels */
    THEMATIC_MOVIES,
    
    /** Sports channels */
    THEMATIC_SPORT,
    
    /** News channels */
    THEMATIC_NEWS,
    
    /** Children's channels */
    THEMATIC_KIDS,
    
    /** Music channels */
    THEMATIC_MUSIC,
    
    /** Documentary channels */
    THEMATIC_DOCUMENTARY,
    
    /** Other thematic channels */
    THEMATIC_OTHER,
    
    /** International channels */
    INTERNATIONAL
}
