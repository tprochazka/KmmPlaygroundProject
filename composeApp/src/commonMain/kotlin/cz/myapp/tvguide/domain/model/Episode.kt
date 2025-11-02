package cz.myapp.tvguide.domain.model

import kotlinx.datetime.Instant

/**
 * Represents a single episode in a TV series.
 * 
 * Used for series with detailed episode information.
 * 
 * @property id Unique identifier
 * @property programId Foreign key to parent Program
 * @property seasonNumber Season number
 * @property episodeNumber Episode number within the season
 * @property title Episode title
 * @property description Episode description
 * @property airDate Original air date
 * @property imageUrl Optional episode-specific image
 * @property duration Episode duration in minutes
 */
data class Episode(
    val id: String,
    val programId: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val title: String,
    val description: String,
    val airDate: Instant,
    val imageUrl: String? = null,
    val duration: Int
) {
    init {
        require(id.isNotBlank()) { "Episode ID must not be blank" }
        require(programId.isNotBlank()) { "Program ID must not be blank" }
        require(seasonNumber > 0) { "Season number must be positive" }
        require(episodeNumber > 0) { "Episode number must be positive" }
        require(title.isNotBlank()) { "Episode title must not be blank" }
        require(duration > 0) { "Duration must be positive" }
    }
}
