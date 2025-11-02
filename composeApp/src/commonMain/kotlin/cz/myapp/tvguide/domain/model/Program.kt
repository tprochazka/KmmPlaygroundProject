package cz.myapp.tvguide.domain.model

import kotlin.time.Instant

/**
 * Represents a TV program scheduled on a channel.
 * 
 * @property id Unique identifier
 * @property channelId Foreign key to Channel
 * @property title Program title
 * @property subtitle Optional episode title or subtitle
 * @property description Full program description
 * @property shortDescription Brief summary (max 200 characters)
 * @property startTime Program start time
 * @property endTime Program end time
 * @property type Program type (movie, series, etc.)
 * @property imageUrl Optional program image/poster URL
 * @property rating Age rating
 * @property isLive Whether this is a live broadcast
 * @property isRepeat Whether this is a repeat/rerun
 * @property isPremiere Whether this is a premiere
 * @property season Optional season number (for series)
 * @property episode Optional episode number (for series)
 * @property year Optional production year
 * @property country Optional production country code
 * @property imdbId Optional IMDB identifier
 * @property csfdId Optional ČSFD identifier
 * @property cast List of cast member IDs
 * @property directors List of director names
 * @property genres List of genres
 * @property createdAt Record creation timestamp
 * @property updatedAt Last update timestamp
 */
data class Program(
    val id: String,
    val channelId: String,
    val title: String,
    val subtitle: String? = null,
    val description: String,
    val shortDescription: String,
    val startTime: Instant,
    val endTime: Instant,
    val type: ProgramType,
    val imageUrl: String? = null,
    val rating: AgeRating,
    val isLive: Boolean = false,
    val isRepeat: Boolean = false,
    val isPremiere: Boolean = false,
    val season: Int? = null,
    val episode: Int? = null,
    val year: Int? = null,
    val country: String? = null,
    val imdbId: String? = null,
    val csfdId: String? = null,
    val cast: List<String> = emptyList(),
    val directors: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val createdAt: Instant,
    val updatedAt: Instant
) {
    init {
        require(id.isNotBlank()) { "Program ID must not be blank" }
        require(channelId.isNotBlank()) { "Channel ID must not be blank" }
        require(title.isNotBlank()) { "Program title must not be blank" }
        require(shortDescription.length <= 200) { 
            "Short description must be max 200 characters" 
        }
        require(endTime > startTime) { 
            "End time must be after start time" 
        }
        season?.let { require(it > 0) { "Season must be positive" } }
        episode?.let { require(it > 0) { "Episode must be positive" } }
        year?.let { require(it in 1900..2100) { "Year must be between 1900 and 2100" } }
    }
    
    /**
     * Program duration in whole minutes (derived from start/end epoch millis).
     */
    val durationMinutes: Long
        get() = (endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()) / 60_000L
}

/**
 * Types of TV programs.
 */
enum class ProgramType {
    /** Feature film */
    MOVIE,
    
    /** TV series episode */
    SERIES,
    
    /** Documentary */
    DOCUMENTARY,
    
    /** News program */
    NEWS,
    
    /** Sports event */
    SPORTS,
    
    /** Talk show */
    TALK_SHOW,
    
    /** Reality show */
    REALITY,
    
    /** Entertainment/variety show */
    ENTERTAINMENT,
    
    /** Children's program */
    KIDS,
    
    /** Music program */
    MUSIC,
    
    /** Educational program */
    EDUCATIONAL,
    
    /** Other/unclassified */
    OTHER
}

/**
 * Age ratings for TV programs.
 */
enum class AgeRating(val displayText: String, val minAge: Int) {
    /** Suitable for all ages */
    ALL("Všichni", 0),
    
    /** Parental guidance recommended for children under 7 */
    PG_7("7+", 7),
    
    /** Not recommended for children under 12 */
    PG_12("12+", 12),
    
    /** Not recommended for children under 15 */
    PG_15("15+", 15),
    
    /** Adults only (18+) */
    ADULT("18+", 18)
}
