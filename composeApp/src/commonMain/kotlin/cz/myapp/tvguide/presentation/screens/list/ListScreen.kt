package cz.myapp.tvguide.presentation.screens.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.coroutines.launch
import kotlin.time.Clock

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
        
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        // Vytvořit seznam dnů: 3 dny zpět, dnes, 7 dní dopředu
        val days = remember(today) {
            (-3..7).map { offset ->
                today.plus(DatePeriod(days = offset))
            }
        }
        
        // Předčíst data pro všechny dny
        LaunchedEffect(days) {
            days.forEach { day ->
                screenModel.preloadDay(day)
            }
        }
        
        val selectedDay = remember { mutableStateOf(screenModel.getSelectedDay()) }

        ListScreenContent(
            days = days,
            selectedDay = selectedDay.value,
            screenModel = screenModel,
            onDaySelected = { day ->
                selectedDay.value = day
                screenModel.setSelectedDay(day)
            },
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
    days: List<LocalDate>,
    selectedDay: LocalDate,
    screenModel: ListScreenModel,
    onDaySelected: (LocalDate) -> Unit,
    onChannelFilterClick: (String) -> Unit,
    onClearFilter: () -> Unit,
    onProgramClick: (Program) -> Unit
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    
    val initialPage = days.indexOf(selectedDay).coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { days.size })
    val coroutineScope = rememberCoroutineScope()
    
    // Synchronizovat pager s vybraným dnem
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage in days.indices) {
            onDaySelected(days[pagerState.currentPage])
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Tab řádek pro navigaci mezi dny
        DayTabRow(
            days = days,
            selectedDay = selectedDay,
            today = today,
            onDaySelected = { day ->
                val pageIndex = days.indexOf(day)
                if (pageIndex >= 0) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pageIndex)
                    }
                }
            }
        )
        
        // Horizontální pager pro dny
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 2 // Předčíst 2 stránky dopředu a dozadu
        ) { page ->
            val dayForPage = days[page]
            val state by screenModel.getStateForDay(dayForPage).collectAsState()
            
            when (state) {
                is ListScreenState.Loading -> {
                    LoadingIndicator(message = "Načítám programy...")
                }
                is ListScreenState.Success -> {
                    val successState = state as ListScreenState.Success
                    
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Channel filter header (pokud je aktivní)
                        if (successState.selectedChannelId != null) {
                            ChannelFilterChip(
                                channelName = successState.programs.firstOrNull()?.first?.name ?: "",
                                onClear = onClearFilter,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        
                        DayProgramList(
                            programs = successState.programs,
                            hasMore = successState.hasMore,
                            onLoadMore = { screenModel.loadMore() },
                            onProgramClick = onProgramClick,
                            onChannelFilterClick = onChannelFilterClick
                        )
                    }
                }
                is ListScreenState.Empty -> {
                    EmptyState(message = (state as ListScreenState.Empty).message)
                }
                is ListScreenState.Error -> {
                    ErrorState(
                        message = (state as ListScreenState.Error).message,
                        onRetry = { screenModel.refresh() }
                    )
                }
            }
        }
    }
}
/**
 * Tab řádek pro navigaci mezi dny
 */
@Composable
private fun DayTabRow(
    days: List<LocalDate>,
    selectedDay: LocalDate,
    today: LocalDate,
    onDaySelected: (LocalDate) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = days.indexOf(selectedDay).coerceAtLeast(0),
        edgePadding = 0.dp
    ) {
        days.forEach { day ->
            Tab(
                selected = selectedDay == day,
                onClick = { onDaySelected(day) },
                text = {
                    Text(
                        text = formatDayTabLabel(day, today),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        }
    }
}

private fun formatDayTabLabel(day: LocalDate, today: LocalDate): String {
    val yesterday = today.minus(DatePeriod(days = 1))
    val tomorrow = today.plus(DatePeriod(days = 1))
    
    return when (day) {
        today -> "Dnes, ${day.dayOfMonth}.${day.monthNumber}."
        yesterday -> "Včera, ${day.dayOfMonth}.${day.monthNumber}."
        tomorrow -> "Zítra, ${day.dayOfMonth}.${day.monthNumber}."
        else -> {
            // Formát: "4.11." nebo "15.11."
            "${day.dayOfMonth}.${day.monthNumber}."
        }
    }
}

/**
 * Seznam programů pro jeden den (bez day separators)
 */
@Composable
private fun DayProgramList(
    programs: List<Pair<Channel, Program>>,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    onProgramClick: (Program) -> Unit,
    onChannelFilterClick: (String) -> Unit
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
    
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Programy bez group by day (už máme tab navigaci)
        items(
            items = programs,
            key = { (channel, program) -> "${channel.id}_${program.id}" }
        ) { (channel, program) ->
            ProgramListItem(
                channel = channel,
                program = program,
                onClick = { onProgramClick(program) },
                onChannelClick = { onChannelFilterClick(channel.id) }
            )
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
