package cz.myapp.tvguide.presentation.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import coil3.compose.AsyncImage
import cz.myapp.tvguide.di.DomainModule
import cz.myapp.tvguide.domain.model.CastMember
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.domain.model.ProgramCast
import cz.myapp.tvguide.presentation.components.*
import cz.myapp.tvguide.presentation.screens.list.ListScreen
import cz.myapp.tvguide.presentation.screens.settings.SettingsScreen
import cz.myapp.tvguide.util.formatTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Detail screen - Shows detailed program information (US5)
 * 
 * Features:
 * - Program poster, title, description
 * - Cast and crew with photos
 * - Ratings (ČSFD, IMDB) with external links
 * - Similar content recommendations
 * - Broadcast schedule across channels
 * - Favorite toggle button
 * - Adaptive layout: scrollable (Compact) vs two-column with sticky poster (Medium+)
 */
data class DetailScreen(
    val programId: String
) : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        
        // Create screen model with dependencies
        val screenModel = remember {
            DetailScreenModel(
                programId = programId,
                getProgramDetailsUseCase = DomainModule.getProgramDetailsUseCase,
                getSimilarProgramsUseCase = DomainModule.getSimilarProgramsUseCase,
                channelRepository = cz.myapp.tvguide.di.DataModule.channelRepository,
                programRepository = cz.myapp.tvguide.di.DataModule.programRepository,
                userPreferencesRepository = cz.myapp.tvguide.di.DataModule.userPreferencesRepository
            )
        }
        
        val state: DetailScreenState by screenModel.state.collectAsState()
        val isFavorite: Boolean by screenModel.isFavorite.collectAsState()
        
        DetailScreenContent(
            state = state,
            isFavorite = isFavorite,
            onRefresh = { screenModel.refresh() },
            onToggleFavorite = { screenModel.toggleFavorite() },
            onSimilarProgramClick = { program ->
                // Navigate to detail of similar program
                navigator?.push(DetailScreen(program.id))
            },
            onCastMemberClick = { castMember ->
                // T129: Navigate to cast member programs list
                navigator?.push(ListScreen(
                    castMemberId = castMember.id,
                    castMemberName = castMember.name
                ))
            },
            onBack = { navigator?.pop() }
        )
    }
}

@Composable
private fun DetailScreenContent(
    state: DetailScreenState,
    isFavorite: Boolean,
    onRefresh: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSimilarProgramClick: (Program) -> Unit,
    onCastMemberClick: (CastMember) -> Unit,
    onBack: () -> Unit
) {
    when (state) {
        is DetailScreenState.Loading -> {
            LoadingIndicator(message = "Načítám detail programu...")
        }
        
        is DetailScreenState.Success -> {
            ProgramDetailContent(
                program = state.program,
                cast = state.cast,
                similarPrograms = state.similarPrograms,
                broadcastSchedule = state.broadcastSchedule,
                isFavorite = isFavorite,
                onRefresh = onRefresh,
                onToggleFavorite = onToggleFavorite,
                onSimilarProgramClick = onSimilarProgramClick,
                onCastMemberClick = onCastMemberClick
            )
        }
        
        is DetailScreenState.Error -> {
            ErrorState(
                message = state.message,
                onRetry = onRefresh
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgramDetailContent(
    program: Program,
    cast: ProgramCast,
    similarPrograms: List<Pair<cz.myapp.tvguide.domain.model.Channel, Program>>,
    broadcastSchedule: List<Pair<cz.myapp.tvguide.domain.model.Channel, Program>>,
    isFavorite: Boolean,
    onRefresh: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSimilarProgramClick: (Program) -> Unit,
    onCastMemberClick: (CastMember) -> Unit
) {
    // Determine layout based on window size
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isCompact = adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    
    if (isCompact) {
        // Compact: Scrollable single column
        CompactLayout(
            program = program,
            cast = cast,
            similarPrograms = similarPrograms,
            broadcastSchedule = broadcastSchedule,
            isFavorite = isFavorite,
            onRefresh = onRefresh,
            onToggleFavorite = onToggleFavorite,
            onSimilarProgramClick = onSimilarProgramClick,
            onCastMemberClick = onCastMemberClick
        )
    } else {
        // Medium/Expanded: Two-column with sticky poster
        TwoColumnLayout(
            program = program,
            cast = cast,
            similarPrograms = similarPrograms,
            broadcastSchedule = broadcastSchedule,
            isFavorite = isFavorite,
            onRefresh = onRefresh,
            onToggleFavorite = onToggleFavorite,
            onSimilarProgramClick = onSimilarProgramClick,
            onCastMemberClick = onCastMemberClick
        )
    }
}

/**
 * Compact layout - single scrollable column.
 */
@Composable
private fun CompactLayout(
    program: Program,
    cast: ProgramCast,
    similarPrograms: List<Pair<cz.myapp.tvguide.domain.model.Channel, Program>>,
    broadcastSchedule: List<Pair<cz.myapp.tvguide.domain.model.Channel, Program>>,
    isFavorite: Boolean,
    onRefresh: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSimilarProgramClick: (Program) -> Unit,
    onCastMemberClick: (CastMember) -> Unit
) {
    val navigator = LocalNavigator.current
    
    Scaffold(
        topBar = {
            TVGuideAppBar(
                title = "Detail pořadu",
                showBackButton = true,
                onBackClick = { navigator?.pop() },
                onSettingsClick = {
                    navigator?.push(SettingsScreen())
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = if (isFavorite) {
                                "Odebrat z oblíbených"
                            } else {
                                "Přidat do oblíbených"
                            },
                            tint = if (isFavorite) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Poster image
            if (program.imageUrl != null) {
                item {
                    ProgramPoster(
                        imageUrl = program.imageUrl,
                        title = program.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    )
                }
            }
            
            // Title and metadata
            item {
                ProgramHeader(
                    program = program,
                    isFavorite = isFavorite,
                    onToggleFavorite = onToggleFavorite
                )
            }
            
            // Description
            item {
                ProgramDescription(program = program)
            }
        
        // Cast and crew
        if (cast.cast.isNotEmpty() || cast.directors.isNotEmpty()) {
            item {
                Text(
                    text = "Obsazení a tvůrci",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            item {
                CastSection(
                    cast = cast,
                    onCastMemberClick = onCastMemberClick
                )
            }
        }
        
        // Ratings section
        item {
            RatingsSection(program = program)
        }
        
        // Broadcast schedule across channels
        if (broadcastSchedule.isNotEmpty()) {
            item {
                Text(
                    text = "Další vysílání",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            item {
                BroadcastScheduleSection(
                    broadcasts = broadcastSchedule,
                    onClick = onSimilarProgramClick
                )
            }
        }
        
        // Similar programs
        if (similarPrograms.isNotEmpty()) {
            item {
                Text(
                    text = "Podobné pořady",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            item {
                SimilarProgramsRow(
                    programs = similarPrograms,
                    onClick = onSimilarProgramClick
                )
            }
        }
        }
    }
}

/**
 * Two-column layout - poster on left, content on right.
 */
@Composable
private fun TwoColumnLayout(
    program: Program,
    cast: ProgramCast,
    similarPrograms: List<Pair<cz.myapp.tvguide.domain.model.Channel, Program>>,
    broadcastSchedule: List<Pair<cz.myapp.tvguide.domain.model.Channel, Program>>,
    isFavorite: Boolean,
    onRefresh: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSimilarProgramClick: (Program) -> Unit,
    onCastMemberClick: (CastMember) -> Unit
) {
    val navigator = LocalNavigator.current
    
    Scaffold(
        topBar = {
            TVGuideAppBar(
                title = "Detail pořadu",
                showBackButton = true,
                onBackClick = { navigator?.pop() },
                onSettingsClick = {
                    navigator?.push(SettingsScreen())
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = if (isFavorite) {
                                "Odebrat z oblíbených"
                            } else {
                                "Přidat do oblíbených"
                            },
                            tint = if (isFavorite) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Left: Sticky poster
            if (program.imageUrl != null) {
                ProgramPoster(
                    imageUrl = program.imageUrl,
                    title = program.title,
                    modifier = Modifier
                        .width(300.dp)
                        .height(450.dp)
                )
            }
            
            // Right: Scrollable content
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title and metadata
                item {
                    ProgramHeader(
                        program = program,
                        isFavorite = isFavorite,
                        onToggleFavorite = onToggleFavorite
                    )
                }
                
                // Description
                item {
                    ProgramDescription(program = program)
                }
                
                // Cast and crew
                if (cast.cast.isNotEmpty() || cast.directors.isNotEmpty()) {
                    item {
                        Text(
                            text = "Obsazení a tvůrci",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                
                item {
                    CastSection(
                        cast = cast,
                        onCastMemberClick = onCastMemberClick
                    )
                }
            }
            
            // Ratings section
            item {
                RatingsSection(program = program)
            }
            
            // Broadcast schedule across channels
            if (broadcastSchedule.isNotEmpty()) {
                item {
                    Text(
                        text = "Další vysílání",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                
                item {
                    BroadcastScheduleSection(
                        broadcasts = broadcastSchedule,
                        onClick = onSimilarProgramClick
                    )
                }
            }
            
            // Similar programs
            if (similarPrograms.isNotEmpty()) {
                item {
                    Text(
                        text = "Podobné pořady",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                
                item {
                    SimilarProgramsRow(
                        programs = similarPrograms,
                        onClick = onSimilarProgramClick
                    )
                }
            }
            }
        }
    }
}

/**
 * Program poster image with Coil.
 */
@Composable
private fun ProgramPoster(
    imageUrl: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Program title, metadata, and favorite button.
 */
@Composable
private fun ProgramHeader(
    program: Program,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Title
        Text(
            text = program.title,
            style = MaterialTheme.typography.headlineMedium
        )
        
        // Subtitle
        if (program.subtitle != null) {
            Text(
                text = program.subtitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Metadata chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            // Genre
            if (program.genres.isNotEmpty()) {
                AssistChip(
                    onClick = { },
                    label = { Text(program.genres.joinToString(", ")) }
                )
            }
            
            // Rating
            AssistChip(
                onClick = { },
                label = { Text(program.rating.displayText) }
            )
            
            // Duration
            AssistChip(
                onClick = { },
                label = { Text("${program.durationMinutes} min") }
            )
            
            // Year
            if (program.year != null) {
                AssistChip(
                    onClick = { },
                    label = { Text(program.year.toString()) }
                )
            }
        }
        
        // Time
        val timeZone = TimeZone.currentSystemDefault()
        Text(
            text = "${program.startTime.formatTime(timeZone = timeZone)} - ${program.endTime.formatTime(timeZone = timeZone)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Program description section.
 */
@Composable
private fun ProgramDescription(program: Program) {
    Text(
        text = program.description,
        style = MaterialTheme.typography.bodyLarge
    )
}

/**
 * Cast and crew section with horizontal scrolling cards.
 */
@Composable
private fun CastSection(
    cast: ProgramCast,
    onCastMemberClick: (CastMember) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Directors
        if (cast.directors.isNotEmpty()) {
            Text(
                text = "Režie: ${cast.directors.joinToString(", ") { it.name }}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // Writers
        if (cast.writers.isNotEmpty()) {
            Text(
                text = "Scénář: ${cast.writers.joinToString(", ") { it.name }}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // Cast members
        if (cast.cast.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cast.cast) { member ->
                    CastMemberCard(
                        member = member,
                        onClick = { onCastMemberClick(member) }
                    )
                }
            }
        }
    }
}

/**
 * Ratings section with ČSFD and IMDB.
 */
@Composable
private fun RatingsSection(program: Program) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ČSFD rating
        if (program.csfdId != null) {
            OutlinedCard(
                onClick = { /* TODO: Open ČSFD link */ },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "ČSFD",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "ID: ${program.csfdId}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        // IMDB rating
        if (program.imdbId != null) {
            OutlinedCard(
                onClick = { /* TODO: Open IMDB link */ },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "IMDB",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "ID: ${program.imdbId}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

/**
 * Horizontal row of similar programs.
 */
@Composable
private fun SimilarProgramsRow(
    programs: List<Pair<cz.myapp.tvguide.domain.model.Channel, Program>>,
    onClick: (Program) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(programs) { (channel, program) ->
            Card(
                onClick = { onClick(program) },
                modifier = Modifier.width(200.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = program.title,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = channel.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (program.genres.isNotEmpty()) {
                        Text(
                            text = program.genres.first(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Broadcast schedule section showing when and where the program will air.
 */
@Composable
private fun BroadcastScheduleSection(
    broadcasts: List<Pair<cz.myapp.tvguide.domain.model.Channel, Program>>,
    onClick: (Program) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        broadcasts.forEach { (channel, program) ->
            Card(
                onClick = { onClick(program) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    // Channel and program info
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = channel.name,
                            style = MaterialTheme.typography.titleSmall
                        )
                        
                        Text(
                            text = "${program.startTime.formatTime()} - ${program.endTime.formatTime()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        if (program.subtitle != null) {
                            Text(
                                text = program.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    
                    // Date
                    val date = program.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    Text(
                        text = "${date.dayOfMonth}.${date.monthNumber}.",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
