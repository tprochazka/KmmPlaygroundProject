# Data Model Specification

**Feature**: TV Program Guide Application  
**Date**: 2025-11-02  
**Phase**: 1 - Core Data Structures

## Overview

This document defines the complete data model for the TV Program Guide application. All entities are designed for Room database storage with cross-platform compatibility (Android, iOS, Desktop, Web).

## Entity Definitions

### 1. Channel

Represents a TV channel in the guide.

```kotlin
@Entity(tableName = "channels")
data class Channel(
    @PrimaryKey
    val id: String,              // Unique identifier (e.g., "ct1", "nova")
    val name: String,            // Display name (e.g., "ČT1", "Nova")
    val logoUrl: String,         // Channel logo image URL
    val category: ChannelCategory, // Channel category
    val number: Int,             // Channel number for sorting
    val description: String,     // Short description
    val languageCode: String,    // ISO 639-1 code (e.g., "cs", "sk")
    val isHd: Boolean,          // HD quality indicator
    val isActive: Boolean,       // Whether channel is currently broadcasting
    val streamUrl: String?,      // Future: Live stream URL
    val createdAt: Instant,      // Record creation time
    val updatedAt: Instant       // Last update time
)

enum class ChannelCategory {
    NATIONAL,        // National public channels (ČT1, ČT2, etc.)
    PRIVATE,         // Private channels (Nova, Prima, etc.)
    REGIONAL,        // Regional channels
    THEMATIC_MOVIES, // Movie channels
    THEMATIC_SPORT,  // Sports channels
    THEMATIC_NEWS,   // News channels
    THEMATIC_KIDS,   // Children's channels
    THEMATIC_MUSIC,  // Music channels
    THEMATIC_DOCUMENTARY, // Documentary channels
    THEMATIC_OTHER,  // Other thematic channels
    INTERNATIONAL    // International channels
}
```

**Validation Rules:**
- `id`: Non-empty, max 50 characters, lowercase alphanumeric + underscore
- `name`: Non-empty, max 100 characters
- `logoUrl`: Valid URL format
- `number`: Positive integer, unique across channels
- `languageCode`: Valid ISO 639-1 code

**Indexes:**
```kotlin
@Entity(
    tableName = "channels",
    indices = [
        Index(value = ["category"]),
        Index(value = ["number"], unique = true),
        Index(value = ["isActive"])
    ]
)
```

### 2. Program

Represents a TV program scheduled on a channel.

```kotlin
@Entity(
    tableName = "programs",
    foreignKeys = [
        ForeignKey(
            entity = Channel::class,
            parentColumns = ["id"],
            childColumns = ["channelId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Program(
    @PrimaryKey
    val id: String,              // Unique identifier
    val channelId: String,       // Foreign key to Channel
    val title: String,           // Program title
    val subtitle: String?,       // Episode title or subtitle
    val description: String,     // Full description
    val shortDescription: String, // Brief summary (max 200 chars)
    val startTime: Instant,      // Program start time
    val endTime: Instant,        // Program end time
    val type: ProgramType,       // Program type
    val imageUrl: String?,       // Program image/poster URL
    val rating: AgeRating,       // Age rating
    val isLive: Boolean,         // Live broadcast indicator
    val isRepeat: Boolean,       // Repeat/rerun indicator
    val isPremiere: Boolean,     // Premiere indicator
    val hasSubtitles: Boolean,   // Subtitle availability
    val hasAudioDescription: Boolean, // Audio description for visually impaired
    val episodeNumber: Int?,     // Episode number
    val seasonNumber: Int?,      // Season number
    val totalEpisodes: Int?,     // Total episodes in series
    val year: Int?,              // Production year
    val countryCode: String?,    // ISO 3166-1 alpha-2 country code
    val csfdUrl: String?,        // ČSFD.cz URL
    val imdbUrl: String?,        // IMDB.com URL
    val createdAt: Instant,      // Record creation time
    val updatedAt: Instant       // Last update time
)

enum class ProgramType {
    MOVIE,           // Feature film
    SERIES,          // TV series
    NEWS,            // News program
    SPORT,           // Sports event
    DOCUMENTARY,     // Documentary
    ENTERTAINMENT,   // Entertainment show
    KIDS,            // Children's program
    MUSIC,           // Music program
    TALK_SHOW,       // Talk show
    REALITY_SHOW,    // Reality show
    EDUCATION,       // Educational content
    OTHER            // Other types
}

enum class AgeRating {
    ALL,             // Suitable for all ages
    TWELVE,          // 12+
    FIFTEEN,         // 15+
    EIGHTEEN         // 18+
}
```

**Validation Rules:**
- `id`: Non-empty, max 50 characters
- `title`: Non-empty, max 200 characters
- `shortDescription`: Max 200 characters
- `description`: Max 2000 characters
- `startTime < endTime`: End must be after start
- `episodeNumber`, `seasonNumber`: Positive integers if present
- `year`: 1900-2100 if present
- `csfdUrl`, `imdbUrl`: Valid URL format if present

**Indexes:**
```kotlin
@Entity(
    tableName = "programs",
    indices = [
        Index(value = ["channelId"]),
        Index(value = ["startTime"]),
        Index(value = ["endTime"]),
        Index(value = ["type"]),
        Index(value = ["channelId", "startTime"])
    ]
)
```

**Computed Properties:**
```kotlin
val Program.durationMinutes: Int
    get() = (endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()).toInt() / 60000

val Program.isCurrentlyAiring: Boolean
    get() {
        val now = Clock.System.now()
        return startTime <= now && endTime > now
    }

val Program.hasEnded: Boolean
    get() = Clock.System.now() > endTime

val Program.progressPercentage: Float
    get() {
        if (!isCurrentlyAiring) return 0f
        val total = endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()
        val elapsed = Clock.System.now().toEpochMilliseconds() - startTime.toEpochMilliseconds()
        return (elapsed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    }
```

### 3. Episode

Detailed information about a series episode (optional extended data).

```kotlin
@Entity(
    tableName = "episodes",
    foreignKeys = [
        ForeignKey(
            entity = Program::class,
            parentColumns = ["id"],
            childColumns = ["programId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Episode(
    @PrimaryKey
    val id: String,              // Unique identifier
    val programId: String,       // Foreign key to Program
    val seasonNumber: Int,       // Season number
    val episodeNumber: Int,      // Episode number
    val episodeTitle: String?,   // Episode-specific title
    val plot: String?,           // Episode plot summary
    val airDate: Instant?,       // Original air date
    val directors: List<String>, // Directors
    val writers: List<String>,   // Writers
    val guestStars: List<String>, // Guest stars
    val imdbRating: Float?,      // IMDB rating (0.0-10.0)
    val csfdRating: Int?,        // ČSFD rating (0-100)
    val createdAt: Instant,
    val updatedAt: Instant
)
```

**Type Converters:**
```kotlin
class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",").filter { it.isNotBlank() }
    }
    
    @TypeConverter
    fun toString(list: List<String>): String {
        return list.joinToString(",")
    }
}
```

### 4. Cast

Cast and crew information for programs.

```kotlin
@Entity(tableName = "cast_members")
data class CastMember(
    @PrimaryKey
    val id: String,              // Unique identifier
    val name: String,            // Full name
    val role: CastRole,          // Role type
    val photoUrl: String?,       // Photo URL
    val biography: String?,      // Short biography
    val birthDate: Instant?,     // Date of birth
    val nationality: String?,    // Nationality
    val imdbUrl: String?,        // IMDB profile URL
    val csfdUrl: String?,        // ČSFD profile URL
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class CastRole {
    ACTOR,
    DIRECTOR,
    WRITER,
    PRODUCER,
    CINEMATOGRAPHER,
    COMPOSER,
    EDITOR,
    OTHER
}

@Entity(
    tableName = "program_cast",
    primaryKeys = ["programId", "castMemberId"],
    foreignKeys = [
        ForeignKey(
            entity = Program::class,
            parentColumns = ["id"],
            childColumns = ["programId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CastMember::class,
            parentColumns = ["id"],
            childColumns = ["castMemberId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProgramCast(
    val programId: String,       // Foreign key to Program
    val castMemberId: String,    // Foreign key to CastMember
    val role: CastRole,          // Role in this program
    val characterName: String?,  // Character name (for actors)
    val order: Int               // Display order
)
```

### 5. FavoriteProgram

User's favorite programs for tracking and notifications.

```kotlin
@Entity(tableName = "favorite_programs")
data class FavoriteProgram(
    @PrimaryKey
    val id: String,              // Unique identifier
    val programId: String,       // Program ID (not FK - can be future programs)
    val programTitle: String,    // Cached title for display
    val channelId: String,       // Channel ID
    val channelName: String,     // Cached channel name
    val startTime: Instant,      // Scheduled start time
    val notifyBefore: Int,       // Minutes before start to notify (0 = no notification)
    val notes: String?,          // User notes
    val addedAt: Instant,        // When added to favorites
    val isNotified: Boolean,     // Whether notification was sent
    val isWatched: Boolean       // Whether user marked as watched
)
```

**Validation Rules:**
- `notifyBefore`: 0, 5, 10, 15, 30, 60 minutes

**Indexes:**
```kotlin
@Entity(
    tableName = "favorite_programs",
    indices = [
        Index(value = ["programId"]),
        Index(value = ["startTime"]),
        Index(value = ["isNotified", "notifyBefore"])
    ]
)
```

### 6. FavoriteChannelList

User's favorite channel lists (ordered collections).

```kotlin
@Entity(tableName = "favorite_channel_lists")
data class FavoriteChannelList(
    @PrimaryKey
    val id: String,              // Unique identifier
    val name: String,            // List name (e.g., "News Channels", "My Favorites")
    val channelIds: List<String>, // Ordered list of channel IDs
    val iconName: String,        // Material icon name
    val color: Long,             // Color as Long (Color.toArgb())
    val order: Int,              // Display order
    val isPinned: Boolean,       // Pin to top
    val createdAt: Instant,
    val updatedAt: Instant
)
```

**Type Converters:**
```kotlin
class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",").filter { it.isNotBlank() }
    }
    
    @TypeConverter
    fun toString(list: List<String>): String {
        return list.joinToString(",")
    }
}

class InstantConverter {
    @TypeConverter
    fun fromTimestamp(value: Long): Instant {
        return Instant.fromEpochMilliseconds(value)
    }
    
    @TypeConverter
    fun toTimestamp(instant: Instant): Long {
        return instant.toEpochMilliseconds()
    }
}
```

**Validation Rules:**
- `name`: Non-empty, max 50 characters, unique
- `channelIds`: Max 100 channels per list
- `iconName`: Valid Material Icons name
- `order`: Unique across lists

### 7. UserPreferences

Application settings and user preferences.

```kotlin
@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey
    val id: String = "default",  // Singleton entity
    val theme: AppTheme,         // Theme preference
    val defaultView: DefaultView, // Default home view
    val navigationStyle: NavigationStyle, // Navigation preference
    val showChannelLogos: Boolean, // Show logos in EPG
    val showProgramImages: Boolean, // Show program images
    val compactMode: Boolean,    // Compact UI mode
    val enable24HourFormat: Boolean, // Time format
    val enableNotifications: Boolean, // Master notification toggle
    val defaultNotifyBefore: Int, // Default notification time (minutes)
    val autoRefreshInterval: Int, // Auto-refresh interval (minutes, 0 = disabled)
    val cacheRetentionDays: Int, // How long to keep cached data
    val preferredLanguage: String, // ISO 639-1 language code
    val lastSyncTime: Instant?,  // Last successful sync
    val updatedAt: Instant
)

enum class AppTheme {
    SYSTEM,          // Follow system theme
    LIGHT,           // Light theme
    DARK             // Dark theme
}

enum class DefaultView {
    NOW_ON_TV,       // Home - Now on TV
    FAVORITES,       // Favorites
    EPG_GRID,        // EPG Grid
    CHRONOLOGICAL_LIST // Chronological list
}

enum class NavigationStyle {
    AUTO,            // Adaptive (bottom bar / nav rail)
    BOTTOM_BAR,      // Always bottom bar
    NAVIGATION_RAIL, // Always navigation rail
    DRAWER           // Always drawer
}
```

**Default Values:**
```kotlin
val UserPreferences.Companion.DEFAULT = UserPreferences(
    id = "default",
    theme = AppTheme.SYSTEM,
    defaultView = DefaultView.NOW_ON_TV,
    navigationStyle = NavigationStyle.AUTO,
    showChannelLogos = true,
    showProgramImages = true,
    compactMode = false,
    enable24HourFormat = true,
    enableNotifications = true,
    defaultNotifyBefore = 15,
    autoRefreshInterval = 60,
    cacheRetentionDays = 7,
    preferredLanguage = "cs",
    lastSyncTime = null,
    updatedAt = Clock.System.now()
)
```

## Database Schema

### Room Database Configuration

```kotlin
@Database(
    entities = [
        Channel::class,
        Program::class,
        Episode::class,
        CastMember::class,
        ProgramCast::class,
        FavoriteProgram::class,
        FavoriteChannelList::class,
        UserPreferences::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    StringListConverter::class,
    InstantConverter::class,
    ChannelCategoryConverter::class,
    ProgramTypeConverter::class,
    AgeRatingConverter::class,
    CastRoleConverter::class,
    AppThemeConverter::class,
    DefaultViewConverter::class,
    NavigationStyleConverter::class
)
abstract class TvGuideDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun programDao(): ProgramDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun castDao(): CastDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun preferencesDao(): PreferencesDao
}
```

### Database Migrations

```kotlin
object Migrations {
    // Future migrations will be added here
    // val MIGRATION_1_2 = object : Migration(1, 2) { ... }
}
```

## Relationships & Queries

### Common Query Patterns

#### 1. Get Current Programs for Channels

```kotlin
@Dao
interface ProgramDao {
    @Query("""
        SELECT * FROM programs
        WHERE channelId IN (:channelIds)
        AND startTime <= :now
        AND endTime > :now
        ORDER BY channelId, startTime
    """)
    suspend fun getCurrentPrograms(channelIds: List<String>, now: Long): List<Program>
}
```

#### 2. Get Programs in Time Range

```kotlin
@Query("""
    SELECT * FROM programs
    WHERE channelId = :channelId
    AND startTime >= :startTime
    AND startTime < :endTime
    ORDER BY startTime
""")
suspend fun getProgramsInRange(
    channelId: String,
    startTime: Long,
    endTime: Long
): List<Program>
```

#### 3. Get Program with Cast

```kotlin
data class ProgramWithCast(
    @Embedded val program: Program,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ProgramCast::class,
            parentColumn = "programId",
            entityColumn = "castMemberId"
        )
    )
    val cast: List<CastMember>
)

@Transaction
@Query("SELECT * FROM programs WHERE id = :programId")
suspend fun getProgramWithCast(programId: String): ProgramWithCast?
```

#### 4. Get Channels with Current Programs

```kotlin
data class ChannelWithCurrentProgram(
    @Embedded val channel: Channel,
    @Relation(
        parentColumn = "id",
        entityColumn = "channelId"
    )
    val programs: List<Program> // Filter in code for current
)

@Transaction
@Query("""
    SELECT DISTINCT c.* FROM channels c
    INNER JOIN programs p ON c.id = p.channelId
    WHERE p.startTime <= :now AND p.endTime > :now
    ORDER BY c.number
""")
suspend fun getChannelsWithCurrentPrograms(now: Long): List<ChannelWithCurrentProgram>
```

## State Transitions

### Program States

```kotlin
sealed interface ProgramState {
    data object NotStarted : ProgramState   // Future program
    data class InProgress(val percentage: Float) : ProgramState // Currently airing
    data object Ended : ProgramState        // Past program
}

fun Program.getState(): ProgramState {
    val now = Clock.System.now()
    return when {
        now < startTime -> ProgramState.NotStarted
        now > endTime -> ProgramState.Ended
        else -> ProgramState.InProgress(progressPercentage)
    }
}
```

### Favorite Notification States

```kotlin
sealed interface NotificationState {
    data object Disabled : NotificationState      // notifyBefore = 0
    data object Scheduled : NotificationState     // isNotified = false, time not reached
    data object Sent : NotificationState          // isNotified = true
    data object Expired : NotificationState       // startTime passed, not sent
}

fun FavoriteProgram.getNotificationState(): NotificationState {
    if (notifyBefore == 0) return NotificationState.Disabled
    if (isNotified) return NotificationState.Sent
    
    val now = Clock.System.now()
    val notifyTime = startTime.minus(notifyBefore.minutes)
    
    return when {
        now >= startTime -> NotificationState.Expired
        now < notifyTime -> NotificationState.Scheduled
        else -> NotificationState.Scheduled // In notification window
    }
}
```

## Data Validation

### Validation Extensions

```kotlin
fun Channel.validate(): List<String> {
    val errors = mutableListOf<String>()
    
    if (id.isBlank() || id.length > 50) {
        errors.add("Channel ID must be 1-50 characters")
    }
    if (name.isBlank() || name.length > 100) {
        errors.add("Channel name must be 1-100 characters")
    }
    if (!logoUrl.isValidUrl()) {
        errors.add("Invalid logo URL")
    }
    if (number <= 0) {
        errors.add("Channel number must be positive")
    }
    
    return errors
}

fun Program.validate(): List<String> {
    val errors = mutableListOf<String>()
    
    if (id.isBlank()) {
        errors.add("Program ID required")
    }
    if (title.isBlank() || title.length > 200) {
        errors.add("Program title must be 1-200 characters")
    }
    if (endTime <= startTime) {
        errors.add("End time must be after start time")
    }
    if (shortDescription.length > 200) {
        errors.add("Short description max 200 characters")
    }
    
    return errors
}

private fun String.isValidUrl(): Boolean {
    return try {
        val url = URL(this)
        url.protocol in listOf("http", "https")
    } catch (e: Exception) {
        false
    }
}
```

## Summary

This data model provides:

- **8 entities**: Channel, Program, Episode, CastMember, ProgramCast, FavoriteProgram, FavoriteChannelList, UserPreferences
- **Room database**: Full schema with relations, indexes, type converters
- **Validation**: Extension functions for data integrity
- **State management**: Computed properties for program/notification states
- **Query patterns**: Common queries for UI requirements
- **Type safety**: Enums for categorical data
- **Relationships**: Proper foreign keys and junction tables

All entities align with spec.md requirements (FR-001 through FR-060) and support Phase 1 prototype implementation with mocked data.
