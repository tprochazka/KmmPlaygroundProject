package cz.myapp.tvguide.util

import kotlinx.datetime.*
import kotlin.time.Duration

/**
 * Extension functions for common operations in TV Guide app.
 */

// ============================================================================
// DateTime Extensions
// ============================================================================

/**
 * Format Instant to time string (HH:mm or h:mm a based on format preference).
 */
fun Instant.formatTime(use24Hour: Boolean = true, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val localDateTime = this.toLocalDateTime(timeZone)
    val hour = localDateTime.hour
    val minute = localDateTime.minute
    
    return if (use24Hour) {
        "%02d:%02d".format(hour, minute)
    } else {
        val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val amPm = if (hour < 12) "AM" else "PM"
        "%d:%02d %s".format(hour12, minute, amPm)
    }
}

/**
 * Format Instant to date string (d.M.yyyy).
 */
fun Instant.formatDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val localDateTime = this.toLocalDateTime(timeZone)
    return "%d.%d.%d".format(
        localDateTime.dayOfMonth,
        localDateTime.monthNumber,
        localDateTime.year
    )
}

/**
 * Format Instant to full datetime string (d.M.yyyy HH:mm).
 */
fun Instant.formatDateTime(
    use24Hour: Boolean = true,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    return "${formatDate(timeZone)} ${formatTime(use24Hour, timeZone)}"
}

/**
 * Format Instant to human-readable relative time (e.g., "2 hours ago", "in 30 minutes").
 */
fun Instant.formatRelative(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val now = Clock.System.now()
    val diff = this - now
    
    return when {
        diff.inWholeMinutes == 0L -> "nyní"
        diff.inWholeMinutes < 0 && diff.inWholeMinutes > -60 -> "před ${-diff.inWholeMinutes} min"
        diff.inWholeMinutes > 0 && diff.inWholeMinutes < 60 -> "za ${diff.inWholeMinutes} min"
        diff.inWholeHours < 0 && diff.inWholeHours > -24 -> "před ${-diff.inWholeHours} h"
        diff.inWholeHours > 0 && diff.inWholeHours < 24 -> "za ${diff.inWholeHours} h"
        diff.inWholeDays < 0 -> "před ${-diff.inWholeDays} dny"
        diff.inWholeDays > 0 -> "za ${diff.inWholeDays} dny"
        else -> formatDate(timeZone)
    }
}

/**
 * Get day name in Czech (Pondělí, Úterý, ...).
 */
fun Instant.getDayName(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val localDateTime = this.toLocalDateTime(timeZone)
    return when (localDateTime.dayOfWeek) {
        DayOfWeek.MONDAY -> "Pondělí"
        DayOfWeek.TUESDAY -> "Úterý"
        DayOfWeek.WEDNESDAY -> "Středa"
        DayOfWeek.THURSDAY -> "Čtvrtek"
        DayOfWeek.FRIDAY -> "Pátek"
        DayOfWeek.SATURDAY -> "Sobota"
        DayOfWeek.SUNDAY -> "Neděle"
        else -> ""
    }
}

/**
 * Get short day name in Czech (Po, Út, St, ...).
 */
fun Instant.getShortDayName(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val localDateTime = this.toLocalDateTime(timeZone)
    return when (localDateTime.dayOfWeek) {
        DayOfWeek.MONDAY -> "Po"
        DayOfWeek.TUESDAY -> "Út"
        DayOfWeek.WEDNESDAY -> "St"
        DayOfWeek.THURSDAY -> "Čt"
        DayOfWeek.FRIDAY -> "Pá"
        DayOfWeek.SATURDAY -> "So"
        DayOfWeek.SUNDAY -> "Ne"
        else -> ""
    }
}

/**
 * Check if this Instant is today.
 */
fun Instant.isToday(timeZone: TimeZone = TimeZone.currentSystemDefault()): Boolean {
    val now = Clock.System.now()
    val thisDate = this.toLocalDateTime(timeZone).date
    val todayDate = now.toLocalDateTime(timeZone).date
    return thisDate == todayDate
}

/**
 * Check if this Instant is in the past.
 */
fun Instant.isPast(): Boolean {
    return this < Clock.System.now()
}

/**
 * Check if this Instant is in the future.
 */
fun Instant.isFuture(): Boolean {
    return this > Clock.System.now()
}

/**
 * Get start of day for this Instant.
 */
fun Instant.startOfDay(timeZone: TimeZone = TimeZone.currentSystemDefault()): Instant {
    val localDateTime = this.toLocalDateTime(timeZone)
    return LocalDateTime(
        localDateTime.year,
        localDateTime.month,
        localDateTime.dayOfMonth,
        0, 0, 0, 0
    ).toInstant(timeZone)
}

/**
 * Get end of day for this Instant.
 */
fun Instant.endOfDay(timeZone: TimeZone = TimeZone.currentSystemDefault()): Instant {
    val localDateTime = this.toLocalDateTime(timeZone)
    return LocalDateTime(
        localDateTime.year,
        localDateTime.month,
        localDateTime.dayOfMonth,
        23, 59, 59, 999_999_999
    ).toInstant(timeZone)
}

// ============================================================================
// Duration Extensions
// ============================================================================

/**
 * Format Duration to human-readable string (e.g., "2h 30min", "45min").
 */
fun Duration.formatDuration(): String {
    val hours = inWholeHours
    val minutes = inWholeMinutes % 60
    
    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}min"
        hours > 0 -> "${hours}h"
        else -> "${minutes}min"
    }
}

// ============================================================================
// String Extensions
// ============================================================================

/**
 * Truncate string to maximum length with ellipsis.
 */
fun String.truncate(maxLength: Int, ellipsis: String = "..."): String {
    return if (length <= maxLength) {
        this
    } else {
        take(maxLength - ellipsis.length) + ellipsis
    }
}

/**
 * Capitalize first letter of each word.
 */
fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

// ============================================================================
// Collection Extensions
// ============================================================================

/**
 * Group list items by a key and preserve order.
 */
fun <T, K> List<T>.groupByPreservingOrder(keySelector: (T) -> K): List<Pair<K, List<T>>> {
    val groups = mutableMapOf<K, MutableList<T>>()
    val keys = mutableListOf<K>()
    
    forEach { item ->
        val key = keySelector(item)
        if (key !in groups) {
            keys.add(key)
            groups[key] = mutableListOf()
        }
        groups[key]!!.add(item)
    }
    
    return keys.map { key -> key to groups[key]!! }
}

// ============================================================================
// Number Extensions
// ============================================================================

/**
 * Format percentage (0.0 to 1.0) to string with 0-2 decimal places.
 */
fun Float.formatPercent(): String {
    val percent = this * 100
    return when {
        percent % 1.0f == 0.0f -> "%.0f%%".format(percent)
        percent % 0.1f == 0.0f -> "%.1f%%".format(percent)
        else -> "%.2f%%".format(percent)
    }
}

/**
 * Clamp value between min and max.
 */
fun Int.clamp(min: Int, max: Int): Int {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}

/**
 * Clamp value between min and max.
 */
fun Float.clamp(min: Float, max: Float): Float {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}
