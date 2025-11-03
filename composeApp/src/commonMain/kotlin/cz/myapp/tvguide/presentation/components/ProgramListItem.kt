package cz.myapp.tvguide.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.util.formatTime
import kotlinx.datetime.TimeZone

/**
 * List item displaying a program in chronological list view.
 * 
 * Shows:
 * - Channel logo (clickable for filtering)
 * - Program time
 * - Program title and description
 * - Genre badge
 * 
 * US4: Chronological List View
 */
@Composable
fun ProgramListItem(
    channel: Channel,
    program: Program,
    onClick: () -> Unit,
    onChannelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Channel logo (clickable for filter)
            Box(
                modifier = Modifier
                    .clickable(onClick = onChannelClick)
                    .padding(4.dp)
            ) {
                ChannelLogo(
                    channel = channel,
                    size = 48.dp
                )
            }
            
            // Program info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Time
                val timeZone = TimeZone.currentSystemDefault()
                Text(
                    text = "${program.startTime.formatTime(timeZone = timeZone)} - ${program.endTime.formatTime(timeZone = timeZone)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Title
                Text(
                    text = program.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Description
                if (!program.description.isNullOrBlank()) {
                    Text(
                        text = program.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Genre badge
                if (program.genres.isNotEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = program.genres.first(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
