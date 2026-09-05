package cz.myapp.tvguide.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import cz.myapp.tvguide.domain.model.CastMember

/**
 * Card displaying a cast or crew member.
 * 
 * Shows:
 * - Photo (if available)
 * - Name
 * - Role (Actor, Director, etc.)
 * - Character name (for actors)
 * 
 * US5: Program Details - Cast and Crew
 */
@Composable
fun CastMemberCard(
    member: CastMember,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Build accessibility description
    val accessibilityDescription = buildString {
        append("${member.role}: ${member.name}")
        if (member.character != null) {
            append(" jako ${member.character}")
        }
    }
    
    Card(
        onClick = onClick,
        modifier = modifier
            .width(120.dp)
            .semantics {
                contentDescription = accessibilityDescription
            }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Photo
            if (member.photoUrl != null) {
                Card(
                    modifier = Modifier.size(80.dp)
                ) {
                    AsyncImage(
                        model = member.photoUrl,
                        contentDescription = member.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                // Placeholder for no photo
                Surface(
                    modifier = Modifier.size(80.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = member.name.take(1),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Name
            Text(
                text = member.name,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Character name (for actors)
            if (member.character != null) {
                Text(
                    text = member.character,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Role badge
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = member.role,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
