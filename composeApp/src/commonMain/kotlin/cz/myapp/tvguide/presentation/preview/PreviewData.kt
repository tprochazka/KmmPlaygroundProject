package cz.myapp.tvguide.presentation.preview

import cz.myapp.tvguide.domain.model.*
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/**
 * Preview data helpers for Compose previews.
 *
 * Provides sample domain models for UI previews without depending on data layer.
 * This maintains Clean Architecture boundaries (Presentation should not depend on Data).
 */

/**
 * Creates a sample Channel for preview purposes.
 */
fun previewChannel(
    id: String = "preview_channel",
    name: String = "Preview Channel",
    number: Int = 1,
    category: ChannelCategory = ChannelCategory.NATIONAL,
    logoUrl: String = "https://example.com/logo.png",
    description: String = "Preview channel description",
    languageCode: String = "cs",
    isHd: Boolean = true,
    isActive: Boolean = true,
    streamUrl: String? = null,
    createdAt: Instant = Clock.System.now(),
    updatedAt: Instant = Clock.System.now()
): Channel = Channel(
    id = id,
    name = name,
    number = number,
    category = category,
    logoUrl = logoUrl,
    description = description,
    languageCode = languageCode,
    isHd = isHd,
    isActive = isActive,
    streamUrl = streamUrl,
    createdAt = createdAt,
    updatedAt = updatedAt
)

/**
 * Creates a sample Program for preview purposes.
 */
fun previewProgram(
    id: String = "preview_program",
    channelId: String = "preview_channel",
    title: String = "Preview Program",
    subtitle: String? = null,
    description: String = "This is a preview program description.",
    shortDescription: String = "Preview program",
    startTime: Instant = Clock.System.now(),
    durationMinutes: Int = 60,
    type: ProgramType = ProgramType.MOVIE,
    imageUrl: String? = null,
    rating: AgeRating = AgeRating.ALL,
    isLive: Boolean = false,
    isRepeat: Boolean = false,
    isPremiere: Boolean = false,
    season: Int? = null,
    episode: Int? = null,
    year: Int? = null,
    country: String? = null,
    imdbId: String? = null,
    csfdId: String? = null,
    cast: List<String> = emptyList(),
    directors: List<String> = emptyList(),
    genres: List<String> = listOf("Drama"),
    createdAt: Instant = Clock.System.now(),
    updatedAt: Instant = Clock.System.now()
): Program {
    val endTime = startTime + durationMinutes.minutes
    return Program(
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
 * Creates a sample CastMember for preview purposes.
 */
fun previewCastMember(
    id: String = "preview_cast",
    name: String = "Preview Actor",
    role: String = "Main Character",
    photoUrl: String? = null
): CastMember = CastMember(
    id = id,
    name = name,
    role = role,
    photoUrl = photoUrl
)

/**
 * Creates a sample FavoriteChannelList for preview purposes.
 */
fun previewFavoriteChannelList(
    id: String = "preview_list",
    userId: String = "preview_user",
    name: String = "Preview Favorites",
    channelIds: List<String> = emptyList(),
    isDefault: Boolean = true,
    createdAt: Instant = Clock.System.now(),
    updatedAt: Instant = Clock.System.now()
): FavoriteChannelList = FavoriteChannelList(
    id = id,
    userId = userId,
    name = name,
    channelIds = channelIds,
    isDefault = isDefault,
    createdAt = createdAt,
    updatedAt = updatedAt
)

/**
 * Creates a sample Episode for preview purposes.
 */
fun previewEpisode(
    id: String = "preview_episode",
    programId: String = "preview_program",
    seasonNumber: Int = 1,
    episodeNumber: Int = 1,
    title: String = "Preview Episode",
    description: String = "Preview episode description",
    airDate: Instant = Clock.System.now(),
    imageUrl: String? = null,
    duration: Int = 60
): Episode = Episode(
    id = id,
    programId = programId,
    seasonNumber = seasonNumber,
    episodeNumber = episodeNumber,
    title = title,
    description = description,
    airDate = airDate,
    imageUrl = imageUrl,
    duration = duration
)
