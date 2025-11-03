package cz.myapp.tvguide.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.myapp.tvguide.domain.model.Channel

/**
 * Channel logo component.
 * 
 * Phase 1: Shows channel number in a circle (placeholder).
 * Phase 2+: Will use Coil to load actual channel logos from URLs.
 * 
 * @param channel Channel to display logo for
 * @param size Size of the logo (diameter)
 * @param modifier Modifier for customization
 */
@Composable
fun ChannelLogo(
    channel: Channel,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    // Phase 1: Simple placeholder with channel number
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .semantics {
                contentDescription = "Logo kanálu ${channel.name}"
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = channel.number.toString(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
    
    // Phase 2: Use Coil to load actual logo
    // AsyncImage(
    //     model = channel.logoUrl,
    //     contentDescription = "${channel.name} logo",
    //     modifier = modifier
    //         .size(size)
    //         .clip(CircleShape),
    //     placeholder = painterResource(Res.drawable.placeholder_channel),
    //     error = painterResource(Res.drawable.placeholder_channel)
    // )
}
