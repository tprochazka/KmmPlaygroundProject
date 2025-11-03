package cz.myapp.tvguide.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.presentation.components.ChannelLogo
import cz.myapp.tvguide.presentation.components.EmptyState
import cz.myapp.tvguide.presentation.components.EpgProgramItem
import cz.myapp.tvguide.presentation.components.LoadingIndicator
import cz.myapp.tvguide.presentation.screens.epg.EpgScreenModel
import cz.myapp.tvguide.util.formatTime
import kotlin.time.Duration.Companion.hours

/**
 * EPG (Electronic Program Guide) screen - Traditional grid view (US3)
 */
object EpgScreen : Screen {
    
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { EpgScreenModel() }
        val state by screenModel.state.collectAsState()
        
        val snackbarHostState = remember { SnackbarHostState() }
        
        // Show error snackbar
        LaunchedEffect(state.error) {
            state.error?.let { error ->
                snackbarHostState.showSnackbar(error)
                screenModel.clearError()
            }
        }
        
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Title and filters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TV Průvodce",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f)
                    )
                    
                    FilterChip(
                        selected = state.showFavoritesOnly,
                        onClick = { screenModel.toggleFavoritesFilter() },
                        label = { Text("Oblíbené") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
                
                HorizontalDivider()
                
                // EPG Grid
                when {
                    state.isLoading -> {
                        LoadingIndicator(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    state.epgData.isEmpty() -> {
                        EmptyState(
                            message = if (state.showFavoritesOnly) {
                                "Žádné oblíbené kanály.\nPřejděte do Oblíbených a vyberte kanály."
                            } else {
                                "Žádná data programu"
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    else -> {
                        EpgGrid(
                            state = state,
                            onProgramClick = { screenModel.selectProgram(it) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EpgGrid(
    state: cz.myapp.tvguide.presentation.screens.epg.EpgState,
    onProgramClick: (Program) -> Unit,
    modifier: Modifier = Modifier
) {
    val pixelsPerHour = 200.dp // Width for one hour of programming
    val channelColumnWidth = 100.dp
    val rowHeight = 60.dp
    
    Row(modifier = modifier) {
        // Left column: Channel logos and names (fixed)
        LazyColumn(
            modifier = Modifier
                .width(channelColumnWidth)
                .fillMaxHeight()
        ) {
            // Header spacer
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
            
            // Channel rows
            items(
                items = state.channels,
                key = { it.id }
            ) { channel ->
                ChannelRow(
                    channel = channel,
                    height = rowHeight
                )
            }
        }
        
        // Right side: Time axis and program grid (scrollable horizontally)
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            // Time axis header
            TimeAxisHeader(
                timeSlots = state.getTimeSlots().take(24), // Show first 24 hours
                pixelsPerHour = pixelsPerHour,
                currentTime = state.currentTime
            )
            
            // Program grid
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = state.channels,
                    key = { it.id }
                ) { channel ->
                    ProgramRow(
                        programs = state.epgData[channel] ?: emptyList(),
                        rowHeight = rowHeight,
                        pixelsPerHour = pixelsPerHour,
                        selectedProgram = state.selectedProgram,
                        onProgramClick = onProgramClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ChannelRow(
    channel: cz.myapp.tvguide.domain.model.Channel,
    height: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChannelLogo(
            channel = channel,
            size = 32.dp,
            modifier = Modifier.padding(end = 8.dp)
        )
        
        Text(
            text = channel.name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2
        )
    }
}

@Composable
private fun TimeAxisHeader(
    timeSlots: List<kotlin.time.Instant>,
    pixelsPerHour: Dp,
    currentTime: kotlin.time.Instant,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .horizontalScroll(scrollState)
    ) {
        timeSlots.forEach { time ->
            Box(
                modifier = Modifier
                    .width(pixelsPerHour)
                    .fillMaxHeight()
                    .background(
                        if (time.epochSeconds / 3600 == currentTime.epochSeconds / 3600) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = time.formatTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (time.epochSeconds / 3600 == currentTime.epochSeconds / 3600) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
private fun ProgramRow(
    programs: List<Program>,
    rowHeight: Dp,
    pixelsPerHour: Dp,
    selectedProgram: Program?,
    onProgramClick: (Program) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(MaterialTheme.colorScheme.surface)
            .horizontalScroll(scrollState)
    ) {
        programs.forEach { program ->
            EpgProgramItem(
                program = program,
                pixelsPerHour = pixelsPerHour,
                onClick = { onProgramClick(program) },
                isSelected = program.id == selectedProgram?.id
            )
        }
    }
}

