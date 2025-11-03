package cz.myapp.tvguide.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.util.formatTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * EPG grid cell component representing a single program.
 * 
 * Width is proportional to program duration.
 * Height matches the channel row height.
 * 
 * @param program The program to display
 * @param pixelsPerHour Width in pixels for one hour of programming
 * @param onClick Callback when the program is clicked
 * @param isSelected Whether this program is currently selected
 * @param modifier Optional modifier
 */
@Composable
fun EpgProgramItem(
    program: Program,
    pixelsPerHour: Dp,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val durationMillis = program.endTime.toEpochMilliseconds() - program.startTime.toEpochMilliseconds()
    val duration = durationMillis.milliseconds
    val width = calculateProgramWidth(duration, pixelsPerHour)
    
    Box(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            .clickable(onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = buildString {
                append(program.startTime.formatTime())
                append(" ")
                append(program.title)
            },
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Calculate the width of a program based on its duration.
 * 
 * @param duration Program duration
 * @param pixelsPerHour Width in pixels for one hour
 * @return Width in Dp
 */
private fun calculateProgramWidth(duration: Duration, pixelsPerHour: Dp): Dp {
    val hours = duration.inWholeMinutes / 60.0
    return pixelsPerHour * hours.toFloat()
}
