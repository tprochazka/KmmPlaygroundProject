package cz.myapp.tvguide.presentation.screens.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.myapp.tvguide.di.DomainModule
import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.presentation.components.EmptyState
import cz.myapp.tvguide.presentation.components.LoadingIndicator
import cz.myapp.tvguide.presentation.components.ProgramListItem
import cz.myapp.tvguide.presentation.screens.detail.DetailScreen
import cz.myapp.tvguide.presentation.components.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * List screen - Shows all programs chronologically (US4)
 * 
 * Features:
 * - Chronological list of all programs
 * - Channel filtering
 * - Day separator headers
 * - Infinite scroll pagination
 * - Pull-to-refresh
 */
class ListScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        // Create screen model with dependencies
        val screenModel = remember {
            ListScreenModel(
                getChronologicalProgramsUseCase = DomainModule.getChronologicalProgramsUseCase
            )
        }
        
        val state: ListScreenState by screenModel.state.collectAsState()
        
        ListScreenContent(
            state = state,
            onRefresh = { screenModel.refresh() },
            onLoadMore = { screenModel.loadMore() },
            onChannelFilterClick = { channelId ->
                screenModel.filterByChannel(channelId)
            },
            onClearFilter = { screenModel.clearChannelFilter() },
            onProgramClick = { program ->
                navigator.push(DetailScreen(program.id))
            }
        )
    }
}

@Composable
private fun ListScreenContent(
    state: ListScreenState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onChannelFilterClick: (String) -> Unit,
    onClearFilter: () -> Unit,
    onProgramClick: (Program) -> Unit
) {
    when (state) {
        is ListScreenState.Loading -> {
            LoadingIndicator(message = "Načítám programy...")
        }
        
        is ListScreenState.Success -> {
            ProgramListContent(
                programs = state.programs,
                selectedChannelId = state.selectedChannelId,
                hasMore = state.hasMore,
                onRefresh = onRefresh,
                onLoadMore = onLoadMore,
                onChannelFilterClick = onChannelFilterClick,
                onClearFilter = onClearFilter,
                onProgramClick = onProgramClick
            )
        }
        
        is ListScreenState.Empty -> {
            EmptyState(message = state.message)
        }
        
        is ListScreenState.Error -> {
            ErrorState(
                message = state.message,
                onRetry = onRefresh
            )
        }
    }
}

@Composable
private fun ProgramListContent(
    programs: List<Pair<Channel, Program>>,
    selectedChannelId: String?,
    hasMore: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onChannelFilterClick: (String) -> Unit,
    onClearFilter: () -> Unit,
    onProgramClick: (Program) -> Unit
) {
    val listState = rememberLazyListState()
    
    // Infinite scroll detection
    val isEndReached by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index != 0 && lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }
    
    // Trigger load more when end is reached
    LaunchedEffect(isEndReached) {
        if (isEndReached && hasMore) {
            onLoadMore()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Channel filter header
        if (selectedChannelId != null) {
            ChannelFilterChip(
                channelName = programs.firstOrNull()?.first?.name ?: "",
                onClear = onClearFilter,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        // Program list
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Group programs by day
            val programsByDay = programs.groupByDay()
            
            programsByDay.forEach { (date, dayPrograms) ->
                // Day separator header
                item(key = "header_$date") {
                    DaySeparator(date = date)
                }
                
                // Programs for this day
                items(
                    items = dayPrograms,
                    key = { (channel, program) -> "${channel.id}_${program.id}" }
                ) { (channel, program) ->
                    ProgramListItem(
                        channel = channel,
                        program = program,
                        onClick = { onProgramClick(program) },
                        onChannelClick = { onChannelFilterClick(channel.id) }
                    )
                }
            }
            
            // Loading more indicator
            if (hasMore) {
                item(key = "loading_more") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(32.dp)
                                .align(androidx.compose.ui.Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Channel filter chip showing current filter with clear button.
 */
@Composable
private fun ChannelFilterChip(
    channelName: String,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = channelName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Zrušit filtr",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Day separator header.
 */
@Composable
private fun DaySeparator(
    date: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

/**
 * Group programs by day for separator headers.
 */
private fun List<Pair<Channel, Program>>.groupByDay(): Map<String, List<Pair<Channel, Program>>> {
    return this.groupBy { (_, program) ->
        val localDate = program.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        // Format as "pondělí 15. dubna 2024"
        val dayOfWeek = when (localDate.dayOfWeek) {
            kotlinx.datetime.DayOfWeek.MONDAY -> "pondělí"
            kotlinx.datetime.DayOfWeek.TUESDAY -> "úterý"
            kotlinx.datetime.DayOfWeek.WEDNESDAY -> "středa"
            kotlinx.datetime.DayOfWeek.THURSDAY -> "čtvrtek"
            kotlinx.datetime.DayOfWeek.FRIDAY -> "pátek"
            kotlinx.datetime.DayOfWeek.SATURDAY -> "sobota"
            kotlinx.datetime.DayOfWeek.SUNDAY -> "neděle"
        }
        
        val month = when (localDate.month) {
            kotlinx.datetime.Month.JANUARY -> "ledna"
            kotlinx.datetime.Month.FEBRUARY -> "února"
            kotlinx.datetime.Month.MARCH -> "března"
            kotlinx.datetime.Month.APRIL -> "dubna"
            kotlinx.datetime.Month.MAY -> "května"
            kotlinx.datetime.Month.JUNE -> "června"
            kotlinx.datetime.Month.JULY -> "července"
            kotlinx.datetime.Month.AUGUST -> "srpna"
            kotlinx.datetime.Month.SEPTEMBER -> "září"
            kotlinx.datetime.Month.OCTOBER -> "října"
            kotlinx.datetime.Month.NOVEMBER -> "listopadu"
            kotlinx.datetime.Month.DECEMBER -> "prosince"
        }
        
        "$dayOfWeek ${localDate.day}. $month ${localDate.year}"
    }
}
