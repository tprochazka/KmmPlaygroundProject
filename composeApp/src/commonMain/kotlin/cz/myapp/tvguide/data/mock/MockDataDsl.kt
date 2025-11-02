package cz.myapp.tvguide.data.mock

import cz.myapp.tvguide.domain.model.*
import kotlin.time.Instant
import kotlin.time.Clock
import kotlin.random.Random

/**
 * DSL for building mock data with fluent API.
 * 
 * Example usage:
 * ```kotlin
 * val channel = mockChannel {
 *     id = "ct1"
 *     name = "ČT1"
 *     category = ChannelCategory.NATIONAL
 *     number = 1
 * }
 * 
 * val program = mockProgram {
 *     title = "Večerníček"
 *     channelId = "ct1"
 *     type = ProgramType.KIDS
 *     startTime = now
 *     duration = 10.minutes
 * }
 * ```
 */

/**
 * Build a mock Channel using DSL.
 */
fun mockChannel(block: ChannelBuilder.() -> Unit): Channel {
    return ChannelBuilder().apply(block).build()
}

/**
 * Build a mock Program using DSL.
 */
fun mockProgram(block: ProgramBuilder.() -> Unit): Program {
    return ProgramBuilder().apply(block).build()
}

/**
 * Build a mock FavoriteChannelList using DSL.
 */
fun mockFavoriteChannelList(block: FavoriteChannelListBuilder.() -> Unit): FavoriteChannelList {
    return FavoriteChannelListBuilder().apply(block).build()
}

/**
 * Builder for Channel entities.
 */
class ChannelBuilder {
    var id: String = "channel_${Random.nextInt(1000, 9999)}"
    var name: String = "Channel $id"
    var logoUrl: String = "https://example.com/logos/$id.png"
    var category: ChannelCategory = ChannelCategory.NATIONAL
    var number: Int = Random.nextInt(1, 999)
    var description: String = "Description for $name"
    var languageCode: String = "cs"
    var isHd: Boolean = false
    var isActive: Boolean = true
    var streamUrl: String? = null
    private val now = Clock.System.now()
    var createdAt: Instant = now
    var updatedAt: Instant = now
    
    fun build(): Channel = Channel(
        id = id,
        name = name,
        logoUrl = logoUrl,
        category = category,
        number = number,
        description = description,
        languageCode = languageCode,
        isHd = isHd,
        isActive = isActive,
        streamUrl = streamUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Builder for Program entities.
 */
class ProgramBuilder {
    var id: String = "program_${Random.nextInt(10000, 99999)}"
    var channelId: String = "ct1"
    var title: String = "Program Title"
    var subtitle: String? = null
    var description: String = "Program description"
    var shortDescription: String = "Short description"
    private val now = Clock.System.now()
    var startTime: Instant = now
    var endTime: Instant = Instant.fromEpochMilliseconds(
        now.toEpochMilliseconds() + 60 * 60 * 1000 // +1 hour
    )
    var type: ProgramType = ProgramType.OTHER
    var imageUrl: String? = null
    var rating: AgeRating = AgeRating.ALL
    var isLive: Boolean = false
    var isRepeat: Boolean = false
    var isPremiere: Boolean = false
    var season: Int? = null
    var episode: Int? = null
    var year: Int? = null
    var country: String? = null
    var imdbId: String? = null
    var csfdId: String? = null
    var cast: List<String> = emptyList()
    var directors: List<String> = emptyList()
    var genres: List<String> = emptyList()
    var createdAt: Instant = now
    var updatedAt: Instant = now
    
    /**
     * Set duration instead of end time (convenience method).
     */
    fun durationMinutes(minutes: Int) {
        endTime = Instant.fromEpochMilliseconds(
            startTime.toEpochMilliseconds() + minutes * 60 * 1000L
        )
    }
    
    fun build(): Program = Program(
        id = id,
        channelId = channelId,
        title = title,
        subtitle = subtitle,
        description = description,
        shortDescription = shortDescription,
        startTime = startTime,
        endTime = endTime,
        type = type,
        imageUrl = imageUrl,
        rating = rating,
        isLive = isLive,
        isRepeat = isRepeat,
        isPremiere = isPremiere,
        season = season,
        episode = episode,
        year = year,
        country = country,
        imdbId = imdbId,
        csfdId = csfdId,
        cast = cast,
        directors = directors,
        genres = genres,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Builder for FavoriteChannelList entities.
 */
class FavoriteChannelListBuilder {
    var id: String = "list_${Random.nextInt(1000, 9999)}"
    var name: String = "My Favorites"
    var channelIds: List<String> = emptyList()
    var isDefault: Boolean = false
    var userId: String = "default"
    private val now = Clock.System.now()
    var createdAt: Instant = now
    var updatedAt: Instant = now
    
    fun build(): FavoriteChannelList = FavoriteChannelList(
        id = id,
        name = name,
        channelIds = channelIds,
        isDefault = isDefault,
        userId = userId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
