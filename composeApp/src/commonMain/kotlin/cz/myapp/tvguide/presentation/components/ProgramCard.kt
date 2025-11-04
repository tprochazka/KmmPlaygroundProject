package cz.myapp.tvguide.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.presentation.theme.LiveIndicatorColor
import cz.myapp.tvguide.util.formatTime
import kotlin.time.Clock
import kotlinx.datetime.TimeZone

/**
 * Card displaying current program for a channel.
 * 
 * Shows:
 * - Channel logo and name
 * - Current program title, time, and description
 * - Live indicator
 * - Progress bar showing how much of program has elapsed
 * 
 * US1: Current TV View
 */
@Composable
fun ProgramCard(
    channel: Channel,
    program: Program?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Build accessibility description
    val timeZone = TimeZone.currentSystemDefault()
    val accessibilityDescription = if (program != null) {
        buildString {
            append("Kanál ${channel.name}. ")
            append("Aktuální pořad: ${program.title}. ")
            append("Čas: ${program.startTime.formatTime(timeZone = timeZone)} až ${program.endTime.formatTime(timeZone = timeZone)}. ")
            if (!program.description.isNullOrBlank()) {
                append("Popis: ${program.description}")
            }
        }
    } else {
        "Kanál ${channel.name}. Žádný program momentálně nevysílá."
    }
    
    Card(
        onClick = onClick,
        modifier = modifier.semantics {
            contentDescription = accessibilityDescription
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Channel header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Channel logo placeholder
                    ChannelLogo(
                        channel = channel,
                        size = 40.dp
                    )
                    
                    Text(
                        text = channel.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Live indicator
                if (program != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.error,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "ŽIVĚ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            if (program != null) {
                // Updated deprecated Divider to HorizontalDivider
                HorizontalDivider()
                
                // Program info
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Title and time
                    Text(
                        text = program.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = "${program.startTime.formatTime(timeZone = timeZone)} - ${program.endTime.formatTime(timeZone = timeZone)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Description
                    if (!program.description.isNullOrBlank()) {
                        Text(
                            text = program.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Progress bar
                    val now = Clock.System.now()
                    val totalDuration = (program.endTime.toEpochMilliseconds() - program.startTime.toEpochMilliseconds()).toFloat()
                    val elapsed = (now.toEpochMilliseconds() - program.startTime.toEpochMilliseconds()).toFloat()
                    val progress = (elapsed / totalDuration).coerceIn(0f, 1f)
                    
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                // No program currently airing
                Text(
                    text = "Žádný program",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
