# Research & Technology Decisions

**Feature**: TV Program Guide Application  
**Date**: 2025-11-02  
**Phase**: 0 - Research

## Overview

This document consolidates technology decisions and research findings for the TV Program Guide Application Phase 1 prototype. All decisions prioritize Kotlin Multiplatform compatibility, Material 3 design, and adaptive layouts across Android, iOS, web, and desktop platforms.

## Image Loading Library

### Decision

**Selected**: Coil 3.x

### Rationale

Coil 3 provides official Compose Multiplatform support with excellent integration:

- **Cross-platform**: Works in commonMain for Android, iOS, Desktop, and Web (Wasm)
- **Compose-first**: `AsyncImage` composable with built-in states (loading, success, error)
- **Performance**: Memory-efficient with automatic lifecycle awareness
- **Features**: Disk/memory caching, transformations, placeholders, error handling
- **Kotlin-native**: Written in Kotlin, coroutines-based, modern API
- **Community**: Large, active community with regular updates

### Alternatives Considered

| Library | Pros | Cons | Decision |
|---------|------|------|----------|
| **Kamel** | KMP-specific, designed for Compose Multiplatform | Smaller community, less mature, fewer features | Rejected - less proven |
| **Landscapist** | Good Android support, multiple image loaders | Primarily Android-focused, KMP support limited | Rejected - poor KMP story |
| **Ktor Client + Manual** | Full control, lightweight | Reinventing wheel, no caching/lifecycle | Rejected - too low-level |

### Implementation Plan

```kotlin
// Dependency
implementation("io.coil-kt.coil3:coil-compose:3.0.0")
implementation("io.coil-kt.coil3:coil-network-ktor:3.0.0")

// Usage
@Composable
fun ChannelLogo(url: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier,
        placeholder = painterResource(Res.drawable.channel_placeholder),
        error = painterResource(Res.drawable.channel_error)
    )
}

// DI configuration
@Provides
@Singleton
fun provideImageLoader(context: PlatformContext): ImageLoader {
    return ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.25) // 25% of available memory
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .maxSizeBytes(50 * 1024 * 1024) // 50 MB
                .directory(context.cacheDir / "image_cache")
                .build()
        }
        .build()
}
```

## EPG Grid Component

### Decision

**Selected**: Fork oleksandrbalan/programguide if needed

### Rationale

The programguide library provides a solid foundation for EPG grids:

- **Proven solution**: Handles complex scrolling, time slots, channel rows
- **Performance**: Optimized for large datasets
- **Customizable**: Can be themed for Material 3

However, the library is Android-focused and may need adaptation:

- **iOS compatibility**: Touch handling may need adjustment
- **Desktop support**: Mouse/keyboard navigation required
- **Web support**: Pointer events for Wasm target

### Implementation Strategy

**Phase 1**: Evaluate compatibility
1. Add library dependency
2. Test on all platforms (Android, iOS, Desktop, Web)
3. Document incompatibilities

**Phase 2**: Adapt if needed
- If compatible: Use directly with Material 3 theming
- If incompatible: Fork to project repo, adapt for KMP, maintain locally
- Consider contributing fixes upstream

**Fallback**: If fork becomes too complex, build custom EPG grid using:
- `LazyVerticalGrid` for channel rows
- `LazyRow` for time slots per channel
- Custom scroll synchronization
- Estimated effort: 1-2 weeks

### Alternative: Custom Implementation

**Pros**: Full control, guaranteed KMP compatibility  
**Cons**: Complex (scrolling sync, performance, time calculations), 1-2 week effort  
**Decision**: Use existing library first, custom only if necessary

## Navigation Architecture

### Decision

**Selected**: Voyager for Phase 1, plan migration to Navigation 3

### Rationale

**Voyager**:
- **KMP-ready**: Official Compose Multiplatform support
- **Mature**: Battle-tested, good documentation
- **Type-safe**: Screen-based navigation with compile-time safety
- **ScreenModel**: ViewModel alternative with lifecycle support
- **Features**: Back stack, nested navigation, transitions

**Navigation 3** (future):
- **Official**: JetBrains solution for Compose Multiplatform
- **Status**: In development, not production-ready yet
- **Migration**: Voyager's screen model maps well to Navigation 3 destinations

### Migration Path

When Navigation 3 becomes stable:

1. Voyager `Screen` → Navigation 3 `Destination`
2. Voyager `ScreenModel` → Navigation 3 `ViewModel` (or keep)
3. Navigator API → Navigation 3 `NavController`
4. Gradual screen-by-screen migration

### Implementation

```kotlin
// Bottom navigation with tabs
@Composable
fun MainNavigation() {
    TabNavigator(HomeTab) { tabNavigator ->
        Scaffold(
            bottomBar = {
                NavigationBar {
                    TabNavigationItem(HomeTab)
                    TabNavigationItem(FavoritesTab)
                    TabNavigationItem(EpgTab)
                    TabNavigationItem(ListTab)
                }
            }
        ) {
            CurrentTab()
        }
    }
}

// Screen definition
class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { HomeScreenModel() }
        val state by screenModel.state.collectAsState()
        
        HomeScreenContent(
            state = state,
            onProgramClick = { program ->
                navigator.push(DetailScreen(program.id))
            }
        )
    }
}
```

## Mock Data Strategy

### Decision

**Selected**: Kotlin DSL with simulated delays

### Rationale

Type-safe DSL provides:
- **Clean syntax**: Readable test data definition
- **Type safety**: Compile-time checks for data structure
- **Realistic behavior**: Delay simulation validates async handling
- **Easy replacement**: Swap mock repos with real ones later
- **Volume testing**: Generate 100+ channels, 1000+ programs easily

### Implementation

```kotlin
// DSL builders
fun channels(block: ChannelListBuilder.() -> Unit): List<Channel> {
    return ChannelListBuilder().apply(block).build()
}

class ChannelListBuilder {
    private val channels = mutableListOf<Channel>()
    
    fun channel(block: ChannelBuilder.() -> Unit) {
        channels.add(ChannelBuilder().apply(block).build())
    }
    
    fun build() = channels.toList()
}

class ChannelBuilder {
    var id: String = ""
    var name: String = ""
    var logoUrl: String = ""
    var category: ChannelCategory = ChannelCategory.NATIONAL
    
    fun build() = Channel(id, name, logoUrl, category, ...)
}

// Mock data definition
val mockChannels = channels {
    channel {
        id = "ct1"
        name = "ČT1"
        logoUrl = "https://example.com/ct1.png"
        category = ChannelCategory.NATIONAL
    }
    channel {
        id = "ct2"
        name = "ČT2"
        logoUrl = "https://example.com/ct2.png"
        category = ChannelCategory.NATIONAL
    }
    // ... 100+ more channels
}

val mockPrograms = programs {
    program {
        id = "p1"
        title = "Zprávy"
        channelId = "ct1"
        startTime = Clock.System.now()
        endTime = Clock.System.now() + 30.minutes
        type = ProgramType.NEWS
    }
    // ... 1000+ programs
}

// Mock repository with delays
class MockProgramRepository : ProgramRepository {
    override suspend fun getCurrentPrograms(channelIds: List<String>): List<Program> {
        delay(300..800) // Simulate network latency
        return mockPrograms.filter { 
            it.channelId in channelIds && it.isCurrentlyAiring 
        }
    }
    
    override suspend fun getProgramDetails(id: String): Program? {
        delay(200..500)
        return mockPrograms.find { it.id == id }
    }
}

// Delay utility
suspend fun delay(range: IntRange) {
    delay(range.random().toLong())
}
```

## Adaptive Layout Strategy

### Decision

**Selected**: Material 3 Adaptive components with WindowSizeClass

### Rationale

Material 3 provides built-in adaptive navigation:
- **NavigationSuiteScaffold**: Automatically switches between bottom bar, nav rail, and drawer
- **WindowSizeClass**: Standardized breakpoints (Compact, Medium, Expanded)
- **Material guidelines**: Follows platform conventions
- **Cross-platform**: Works on all Compose Multiplatform targets

### Breakpoint Strategy

| Size Class | Devices | Navigation | Detail Panel |
|------------|---------|------------|--------------|
| **Compact** | Phones (<600dp) | Bottom bar | Full-screen overlay |
| **Medium** | Small tablets, foldables (600-840dp) | Nav rail | Bottom sheet or side panel (40%) |
| **Expanded** | Large tablets, desktop (>840dp) | Nav rail | Persistent side panel (30-40%) |

### Implementation

```kotlin
@Composable
fun AdaptiveScaffold(
    windowSizeClass: WindowSizeClass,
    selectedDestination: Destination,
    onDestinationSelected: (Destination) -> Unit,
    content: @Composable () -> Unit
) {
    val navigationType = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> NavigationSuiteType.NavigationBar
        WindowWidthSizeClass.Medium -> NavigationSuiteType.NavigationRail
        WindowWidthSizeClass.Expanded -> NavigationSuiteType.NavigationRail
        else -> NavigationSuiteType.NavigationBar
    }
    
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            destinations.forEach { destination ->
                item(
                    selected = selectedDestination == destination,
                    onClick = { onDestinationSelected(destination) },
                    icon = { Icon(destination.icon, destination.label) },
                    label = { Text(destination.label) }
                )
            }
        },
        layoutType = navigationType
    ) {
        content()
    }
}

// Side panel for detail on large screens
@Composable
fun EpgWithAdaptiveDetail(
    windowSizeClass: WindowSizeClass,
    selectedProgram: Program?,
    onProgramSelected: (Program) -> Unit
) {
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            // Full-screen grid, detail as separate screen
            EpgGrid(onProgramClick = onProgramSelected)
        }
        WindowWidthSizeClass.Medium -> {
            // Grid with modal bottom sheet
            EpgGrid(onProgramClick = onProgramSelected)
            selectedProgram?.let {
                ModalBottomSheet {
                    ProgramDetail(it)
                }
            }
        }
        WindowWidthSizeClass.Expanded -> {
            // Grid + persistent side panel
            Row {
                EpgGrid(
                    modifier = Modifier.weight(0.6f),
                    onProgramClick = onProgramSelected
                )
                ProgramDetailPanel(
                    program = selectedProgram,
                    modifier = Modifier.weight(0.4f)
                )
            }
        }
    }
}
```

## Logging Solution

### Decision

**Selected**: Kermit

### Rationale

Kermit is the de-facto standard for Kotlin Multiplatform logging:
- **KMP-native**: Works in commonMain across all platforms
- **Platform integration**: Uses Logcat (Android), OSLog (iOS), console (JS/Desktop)
- **Lightweight**: Minimal overhead
- **Flexible**: Tag-based, severity levels, custom loggers
- **Production-ready**: Crash reporting integration (Crashlytics, Sentry)

### Implementation

```kotlin
// Dependency
implementation("co.touchlab:kermit:2.0.2")

// Usage in common code
object AppLogger {
    private val logger = Logger.withTag("TVGuide")
    
    fun d(message: String) = logger.d { message }
    fun i(message: String) = logger.i { message }
    fun w(message: String, throwable: Throwable? = null) {
        logger.w(throwable) { message }
    }
    fun e(message: String, throwable: Throwable? = null) {
        logger.e(throwable) { message }
    }
}

// In ViewModels/ScreenModels
class HomeScreenModel : ScreenModel {
    init {
        AppLogger.d("HomeScreenModel initialized")
        loadCurrentPrograms()
    }
    
    private fun loadCurrentPrograms() {
        screenModelScope.launch {
            try {
                AppLogger.i("Loading current programs")
                val programs = getCurrentProgramsUseCase()
                AppLogger.d("Loaded ${programs.size} programs")
                _state.value = State.Success(programs)
            } catch (e: Exception) {
                AppLogger.e("Failed to load programs", e)
                _state.value = State.Error(e.message)
            }
        }
    }
}
```

## Summary of Decisions

| Technology | Decision | Rationale |
|------------|----------|-----------|
| **Image Loading** | Coil 3 | KMP support, Compose-first, mature |
| **EPG Grid** | oleksandrbalan/programguide (fork if needed) | Proven solution, may need KMP adaptation |
| **Navigation** | Voyager → Navigation 3 (future) | KMP-ready now, official solution later |
| **Mock Data** | Kotlin DSL with delays | Type-safe, realistic, easy to replace |
| **Adaptive Layout** | Material 3 + WindowSizeClass | Built-in, follows guidelines, cross-platform |
| **Logging** | Kermit | KMP standard, platform integration |

All decisions align with the project constitution:
- ✅ Multiplatform-First (all libraries KMP-compatible)
- ✅ Compose-Native UI (Material 3, Compose components)
- ✅ Code Quality (type-safe DSL, proven libraries)
- ✅ UX Excellence (adaptive layouts, Material 3 design)
