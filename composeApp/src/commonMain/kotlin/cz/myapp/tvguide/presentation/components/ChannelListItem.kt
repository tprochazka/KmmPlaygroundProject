package cz.myapp.tvguide.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.ChannelCategory

/**
 * Reusable component for displaying a channel in a selectable list.
 * 
 * Used in Favorites screen for channel selection and management.
 * 
 * @param channel The channel to display
 * @param isSelected Whether this channel is currently selected/favorited
 * @param onToggle Callback when the checkbox is toggled
 * @param modifier Optional modifier for the component
 */
@Composable
fun ChannelListItem(
    channel: Channel,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle(!isSelected) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Channel logo
        ChannelLogo(
            channel = channel,
            size = 48.dp,
            modifier = Modifier
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Channel info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = channel.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Kanál ${channel.number}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = getCategoryLabel(channel.category),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        // Selection checkbox
        Checkbox(
            checked = isSelected,
            onCheckedChange = onToggle
        )
    }
}

/**
 * Get localized label for channel category.
 */
private fun getCategoryLabel(category: ChannelCategory): String {
    return when (category) {
        ChannelCategory.NATIONAL -> "Národní"
        ChannelCategory.PRIVATE -> "Soukromé"
        ChannelCategory.REGIONAL -> "Regionální"
        ChannelCategory.THEMATIC_MOVIES -> "Filmové"
        ChannelCategory.THEMATIC_SPORT -> "Sportovní"
        ChannelCategory.THEMATIC_NEWS -> "Zpravodajské"
        ChannelCategory.THEMATIC_KIDS -> "Dětské"
        ChannelCategory.THEMATIC_MUSIC -> "Hudební"
        ChannelCategory.THEMATIC_DOCUMENTARY -> "Dokumentární"
        ChannelCategory.THEMATIC_OTHER -> "Ostatní"
        ChannelCategory.INTERNATIONAL -> "Mezinárodní"
    }
}
