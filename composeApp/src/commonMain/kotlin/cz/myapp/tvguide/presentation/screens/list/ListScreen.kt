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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.myapp.tvguide.di.DomainModule
import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.presentation.components.EmptyState
import cz.myapp.tvguide.presentation.components.LoadingIndicator
import cz.myapp.tvguide.presentation.components.ProgramListItem
import cz.myapp.tvguide.presentation.components.TVGuideAppBar
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
 * - Cast member filtering (T129)
 * - Day separator headers
 * - Infinite scroll pagination
 * - Pull-to-refresh
 * 
 * @param castMemberId Optional cast member ID to filter programs
 * @param castMemberName Optional cast member name for display
 */
data class ListScreen(
    val castMemberId: String? = null,
    val castMemberName: String? = null
) : Screen {
    
    companion object {
        // Cache for screen models to persist across tab switches
        private val screenModelCache = mutableMapOf<String, ListScreenModel>()
        
        private fun getOrCreateScreenModel(castMemberId: String?): ListScreenModel {
            val key = castMemberId ?: "default"
            return screenModelCache.getOrPut(key) {
                ListScreenModel(
                    getChronologicalProgramsUseCase = DomainModule.getChronologicalProgramsUseCase,
                    getProgramsByCastMemberUseCase = if (castMemberId != null) {
                        DomainModule.getProgramsByCastMemberUseCase
                    } else {
                        null
                    },
                    castMemberId = castMemberId
                )
            }
        }
    }
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        
        // Get cached screen model - persists across tab switches
        val screenModel = remember(castMemberId) {
            getOrCreateScreenModel(castMemberId)
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
            castMemberName = castMemberName,
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
            },
            onBack = { navigator.pop() }
        )
    }
}

@Composable
private fun ListScreenContent(
    days: List<LocalDate>,
    selectedDay: LocalDate,
    screenModel: ListScreenModel,
    castMemberName: String?,
    onDaySelected: (LocalDate) -> Unit,
    onChannelFilterClick: (String) -> Unit,
    onClearFilter: () -> Unit,
    onProgramClick: (Program) -> Unit,
    onBack: () -> Unit
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isCompact = adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    
    if (isCompact) {
        // Phone layout: ViewPager with horizontal swipe
        CompactListLayout(
            days = days,
            selectedDay = selectedDay,
            today = today,
            screenModel = screenModel,
            castMemberName = castMemberName,
            onDaySelected = onDaySelected,
            onChannelFilterClick = onChannelFilterClick,
            onClearFilter = onClearFilter,
            onProgramClick = onProgramClick,
            onBack = onBack
        )
    } else {
        // Tablet/Desktop layout: Multi-column view showing multiple days
        MultiDayLayout(
            days = days,
            selectedDay = selectedDay,
            today = today,
            screenModel = screenModel,
            adaptiveInfo = adaptiveInfo,
            castMemberName = castMemberName,
            onDaySelected = onDaySelected,
            onChannelFilterClick = onChannelFilterClick,
            onClearFilter = onClearFilter,
            onProgramClick = onProgramClick,
            onBack = onBack
        )
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

/**
 * Compact layout for phones: ViewPager with horizontal day swipe
 */
@Composable
private fun CompactListLayout(
    days: List<LocalDate>,
    selectedDay: LocalDate,
    today: LocalDate,
    screenModel: ListScreenModel,
    castMemberName: String?,
    onDaySelected: (LocalDate) -> Unit,
    onChannelFilterClick: (String) -> Unit,
    onClearFilter: () -> Unit,
    onProgramClick: (Program) -> Unit,
    onBack: () -> Unit
) {
    val navigator = LocalNavigator.currentOrThrow
        
    val initialPage = days.indexOf(selectedDay).coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { days.size })
    val coroutineScope = rememberCoroutineScope()
    
    // Synchronizovat pager s vybraným dnem
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage in days.indices) {
            onDaySelected(days[pagerState.currentPage])
        }
    }
    
    Scaffold(
        topBar = {
            TVGuideAppBar(
                title = "Seznam",
                onSettingsClick = {
                    navigator.push(cz.myapp.tvguide.presentation.screens.settings.SettingsScreen())
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Cast member filter chip (if active)
            if (castMemberName != null) {
                FilterChip(
                    selected = true,
                    onClick = onBack,
                    label = { Text("Filtr: $castMemberName") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Zrušit filtr"
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
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
                DayColumn(
                    day = dayForPage,
                    screenModel = screenModel,
                    onChannelFilterClick = onChannelFilterClick,
                    onClearFilter = onClearFilter,
                    onProgramClick = onProgramClick
                )
            }
        }
    }
}

/**
 * Multi-day layout for tablets: 2-3 columns showing days side-by-side
 */
@Composable
private fun MultiDayLayout(
    days: List<LocalDate>,
    selectedDay: LocalDate,
    today: LocalDate,
    screenModel: ListScreenModel,
    adaptiveInfo: androidx.compose.material3.adaptive.WindowAdaptiveInfo,
    castMemberName: String?,
    onDaySelected: (LocalDate) -> Unit,
    onChannelFilterClick: (String) -> Unit,
    onClearFilter: () -> Unit,
    onProgramClick: (Program) -> Unit,
    onBack: () -> Unit
) {
    val columns = when (adaptiveInfo.windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.MEDIUM -> 2
        WindowWidthSizeClass.EXPANDED -> 3
        else -> 2
    }
    
    // Get the days to show: selected day and next days
    val visibleDays = remember(selectedDay, columns) {
        val selectedIndex = days.indexOf(selectedDay).coerceAtLeast(0)
        days.subList(selectedIndex, (selectedIndex + columns).coerceAtMost(days.size))
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Cast member filter chip (if active)
        if (castMemberName != null) {
            FilterChip(
                selected = true,
                onClick = onBack,
                label = { Text("Filtr: $castMemberName") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Zrušit filtr"
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        // Tab row for day selection
        DayTabRow(
            days = days,
            selectedDay = selectedDay,
            today = today,
            onDaySelected = onDaySelected
        )
        
        // Multi-column layout
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            visibleDays.forEach { day ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Day header
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = if (day == selectedDay) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        tonalElevation = 2.dp
                    ) {
                        Text(
                            text = formatDayTabLabel(day, today),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(12.dp),
                            color = if (day == selectedDay) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    
                    // Day content
                    DayColumn(
                        day = day,
                        screenModel = screenModel,
                        onChannelFilterClick = onChannelFilterClick,
                        onClearFilter = onClearFilter,
                        onProgramClick = onProgramClick
                    )
                }
            }
        }
    }
}

/**
 * Single day column content (shared by both compact and multi-day layouts)
 */
@Composable
private fun DayColumn(
    day: LocalDate,
    screenModel: ListScreenModel,
    onChannelFilterClick: (String) -> Unit,
    onClearFilter: () -> Unit,
    onProgramClick: (Program) -> Unit
) {
    val state by screenModel.getStateForDay(day).collectAsState()
    
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
