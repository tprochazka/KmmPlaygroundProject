# Implementation Plan: TV Program Guide Application

**Branch**: `001-tv-guide-app` | **Date**: 2025-11-02 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/001-tv-guide-app/spec.md`

## Summary

TV Program Guide is a cross-platform application for Android, iOS, web, and desktop that provides comprehensive TV schedule information with personalized features. The first phase focuses on creating a functional prototype with mocked data, adaptive UI layouts, and core navigation. Key features include EPG grid view, program listings, favorites management, and detailed program information with recommendations. The app prioritizes excellent UX for users ranging from beginners to power users, with Material 3 design and responsive layouts optimized for all screen sizes.

**Technical Approach**: Kotlin Multiplatform with Compose UI, Metro DI for dependency injection, Voyager for navigation (with planned migration to Navigation 3), mocked repository layer using Kotlin DSL with simulated delays, and adaptive layouts using Material 3 Adaptive components.

## Technical Context

**Language/Version**: Kotlin 2.2.21, Compose Multiplatform 1.9.1  
**Primary Dependencies**: 
- Compose Multiplatform 1.9.1 (UI framework)
- Metro DI (dependency injection) - https://github.com/ZacSweers/metro
- Voyager (navigation) - https://github.com/adrielcafe/voyager
- Ktor 3.x (HTTP client for future API integration)
- Room (local database for offline data)
- Program Guide library - https://github.com/oleksandrbalan/programguide (may require fork)
- Kermit (logging) - https://github.com/touchlab/Kermit
- Image loading library (TBD - research Coil vs Kamel vs Landscapist)

**Storage**: Room database for offline caching, local preferences for settings  
**Testing**: kotlin-test (common tests), JUnit (Android), XCTest (iOS), Compose UI Testing  
**Target Platform**: Android API 24+, iOS 15+, Web (Wasm primary, JS fallback), Desktop (JVM)  
**Project Type**: Mobile-first KMP application with web and desktop support  
**Performance Goals**: 
- 60fps scrolling in EPG grid and program lists
- <3s app launch to "Now on TV" screen
- <1s program detail page load
- <100ms UI interaction response time

**Constraints**: 
- Offline-capable with cached data
- Adaptive layouts for phones, tablets, foldables, desktop
- Material 3 design compliance
- WCAG AA accessibility minimum

**Scale/Scope**: 
- 12 user stories (P1-P4 priority levels)
- ~20 screens/views across platforms
- Support for 100+ channels
- 7 days of EPG data cached locally

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### I. Multiplatform-First Architecture ✅

- **Status**: COMPLIANT
- **Rationale**: Using Kotlin Multiplatform with shared Compose UI across all platforms (Android, iOS, web, desktop). Repository layer, domain logic, and UI components are in commonMain. Platform-specific code limited to exact alarms (Android) and local notifications (iOS).

### II. Compose-Native UI (NON-NEGOTIABLE) ✅

- **Status**: COMPLIANT
- **Rationale**: 100% Compose Multiplatform for UI. Material 3 design system. Adaptive layouts using Material 3 Adaptive components for different screen sizes. No XML layouts, no SwiftUI UI logic, no web frameworks.

### III. Test-First Development (NON-NEGOTIABLE) ⚠️

- **Status**: DEFERRED TO IMPLEMENTATION
- **Rationale**: Phase 1 focuses on prototype with mocked data. TDD will be enforced starting Phase 2 when implementing real functionality. Prototype serves as acceptance test baseline.
- **Justification**: Common practice to build UI prototype first to validate UX with stakeholders before investing in comprehensive test coverage. Tests will be written before removing mocks.

### IV. Clean Architecture with Clear Boundaries ✅

- **Status**: COMPLIANT
- **Rationale**: 
  - **Presentation**: Compose UI + ViewModels (screen models in Voyager)
  - **Domain**: Use cases, business logic, domain models
  - **Data**: Repository pattern with mocked implementations (Phase 1), real implementations later
  - Dependencies flow inward: Presentation → Domain → Data

### V. Metro DI for Dependency Injection ✅

- **Status**: COMPLIANT
- **Rationale**: Metro DI used for all dependency injection. ViewModels, repositories, and use cases registered in DI container. No manual dependency management except Compose remember.

### VI. User Experience Excellence ✅

- **Status**: COMPLIANT
- **Rationale**: 
  - Material 3 design with adaptive layouts
  - 60fps performance target
  - WCAG AA accessibility
  - Side panel for details on large screens
  - Multi-day view in lists on tablets/desktop
  - Focus on simple UX for beginners with advanced features accessible

### VII. Code Quality and Clarity ✅

- **Status**: COMPLIANT
- **Rationale**: Kotlin conventions, KDoc for public APIs, ktlint enforcement, peer review required. Code will be idiomatic Kotlin with clear structure.

**GATE RESULT**: ✅ PASS (with TDD deferred justification)

## Project Structure

### Documentation (this feature)

```text
specs/001-tv-guide-app/
├── spec.md              # Feature specification (COMPLETE)
├── plan.md              # This file (IN PROGRESS)
├── research.md          # Phase 0 research decisions
├── data-model.md        # Phase 1 data model
├── quickstart.md        # Phase 1 developer guide
├── contracts/           # Phase 1 API contracts (for future integration)
│   └── epg-api.yaml     # EPG data provider contract (OpenAPI)
└── checklists/
    └── requirements.md  # Spec quality checklist (COMPLETE)
```

### Source Code (Kotlin Multiplatform structure)

```text
composeApp/
├── src/
│   ├── commonMain/
│   │   ├── kotlin/
│   │   │   ├── cz/myapp/tvguide/
│   │   │   │   ├── App.kt                    # App entry point
│   │   │   │   ├── di/                       # Metro DI modules
│   │   │   │   │   ├── AppModule.kt
│   │   │   │   │   ├── DataModule.kt
│   │   │   │   │   └── DomainModule.kt
│   │   │   │   ├── domain/                   # Business logic
│   │   │   │   │   ├── model/                # Domain models
│   │   │   │   │   │   ├── Channel.kt
│   │   │   │   │   │   ├── Program.kt
│   │   │   │   │   │   ├── Episode.kt
│   │   │   │   │   │   ├── FavoriteProgram.kt
│   │   │   │   │   │   └── User.kt
│   │   │   │   │   ├── repository/           # Repository interfaces
│   │   │   │   │   │   ├── ChannelRepository.kt
│   │   │   │   │   │   ├── ProgramRepository.kt
│   │   │   │   │   │   └── UserPreferencesRepository.kt
│   │   │   │   │   └── usecase/              # Use cases
│   │   │   │   │       ├── GetCurrentProgramsUseCase.kt
│   │   │   │   │       ├── GetFavoriteChannelsUseCase.kt
│   │   │   │   │       └── GetProgramDetailsUseCase.kt
│   │   │   │   ├── data/                     # Data layer
│   │   │   │   │   ├── repository/           # Repository implementations
│   │   │   │   │   │   ├── MockChannelRepository.kt
│   │   │   │   │   │   ├── MockProgramRepository.kt
│   │   │   │   │   │   └── LocalUserPreferencesRepository.kt
│   │   │   │   │   ├── local/                # Local storage
│   │   │   │   │   │   ├── dao/              # Room DAOs
│   │   │   │   │   │   ├── entity/           # Room entities
│   │   │   │   │   │   └── AppDatabase.kt
│   │   │   │   │   └── mock/                 # Mock data DSL
│   │   │   │   │       ├── MockDataProvider.kt
│   │   │   │   │       └── DelaySimulator.kt
│   │   │   │   ├── presentation/             # UI layer
│   │   │   │   │   ├── theme/                # Material 3 theme
│   │   │   │   │   │   ├── Color.kt
│   │   │   │   │   │   ├── Theme.kt
│   │   │   │   │   │   └── Typography.kt
│   │   │   │   │   ├── navigation/           # Voyager navigation
│   │   │   │   │   │   └── NavGraph.kt
│   │   │   │   │   ├── components/           # Reusable components
│   │   │   │   │   │   ├── ChannelLogo.kt
│   │   │   │   │   │   ├── ProgramCard.kt
│   │   │   │   │   │   ├── AdaptiveScaffold.kt
│   │   │   │   │   │   └── EpgGrid.kt
│   │   │   │   │   └── screens/              # Feature screens
│   │   │   │   │       ├── home/             # Now on TV (P1)
│   │   │   │   │       │   ├── HomeScreen.kt
│   │   │   │   │       │   └── HomeScreenModel.kt
│   │   │   │   │       ├── favorites/        # Favorite channels (P1)
│   │   │   │   │       │   ├── FavoritesScreen.kt
│   │   │   │   │       │   └── FavoritesScreenModel.kt
│   │   │   │   │       ├── epg/              # EPG grid (P2)
│   │   │   │   │       │   ├── EpgScreen.kt
│   │   │   │   │       │   └── EpgScreenModel.kt
│   │   │   │   │       ├── list/             # Chronological list (P2)
│   │   │   │   │       │   ├── ListScreen.kt
│   │   │   │   │       │   └── ListScreenModel.kt
│   │   │   │   │       ├── detail/           # Program details (P2)
│   │   │   │   │       │   ├── DetailScreen.kt
│   │   │   │   │       │   └── DetailScreenModel.kt
│   │   │   │   │       └── settings/         # Settings
│   │   │   │   │           ├── SettingsScreen.kt
│   │   │   │   │           └── SettingsScreenModel.kt
│   │   │   │   └── util/                     # Utilities
│   │   │   │       ├── Logger.kt
│   │   │   │       └── Extensions.kt
│   │   └── resources/                        # Compose resources
│   │       ├── drawable/                     # Images
│   │       └── values/                       # Strings, etc.
│   ├── androidMain/
│   │   └── kotlin/
│   │       └── cz/myapp/tvguide/
│   │           ├── MainActivity.kt
│   │           └── TvGuideApplication.kt
│   ├── iosMain/
│   │   └── kotlin/
│   │       └── cz/myapp/tvguide/
│   │           └── MainViewController.kt
│   ├── jvmMain/                              # Desktop
│   │   └── kotlin/
│   │       └── cz/myapp/tvguide/
│   │           └── Main.kt
│   ├── wasmJsMain/                           # Web (Wasm)
│   │   └── kotlin/
│   │       └── cz/myapp/tvguide/
│   │           └── Main.kt
│   ├── commonTest/                           # Shared tests
│   │   └── kotlin/
│   ├── androidUnitTest/                      # Android tests
│   │   └── kotlin/
│   └── iosTest/                              # iOS tests
│       └── kotlin/
└── build.gradle.kts
```

**Structure Decision**: Kotlin Multiplatform mobile-first structure selected. Maximizes code sharing via commonMain (UI, domain, data layers). Platform-specific code only for app entry points and platform APIs (notifications, alarms). This aligns with Constitution Principle I (Multiplatform-First Architecture) and supports all target platforms (Android, iOS, web, desktop).

## Phase 0: Research & Technology Decisions

### Image Loading Library Selection

**Decision**: Coil 3 (with Compose Multiplatform support)

**Rationale**:
- **Coil 3.x** has official Compose Multiplatform support (commonMain)
- Mature, well-maintained, Kotlin-first
- Excellent Compose integration with `AsyncImage`
- Supports all target platforms (Android, iOS, Desktop, Web via Wasm)
- Built-in caching, placeholder, error handling
- Memory efficient with lifecycle awareness

**Alternatives Considered**:
- **Kamel**: KMP-specific but less mature, smaller community
- **Landscapist**: Good but more Android-focused, Coil has better KMP story
- **ktor-client + manual**: Too low-level, reinventing wheel

**Implementation**: Use Coil's `AsyncImage` composable for channel logos, program posters, cast photos. Configure shared image cache in DI.

### EPG Grid Component Strategy

**Decision**: Fork oleksandrbalan/programguide if needed for KMP compatibility

**Rationale**:
- Original library is Android-focused
- May need adaptation for iOS touch handling, desktop mouse/keyboard
- Fork allows customization for Material 3 theming
- Can contribute fixes upstream

**Alternative**: Build custom EPG grid from scratch - rejected due to complexity (scrolling, time slots, channel rows, performance optimization already solved)

**Implementation**: 
1. Evaluate programguide library compatibility with Compose Multiplatform
2. If compatible: use directly with configuration
3. If incompatible: fork, adapt for KMP, maintain in project repo

### Navigation Architecture

**Decision**: Voyager for Phase 1, plan migration to Navigation 3 when stable

**Rationale**:
- Voyager is KMP-ready, mature, well-documented
- Type-safe navigation with screen models (similar to ViewModels)
- Good integration with Compose Multiplatform
- Navigation 3 for Compose Multiplatform is in development, not production-ready
- Migration path: Voyager's screen-based model maps well to Navigation 3's destination concept

**Implementation**:
- Define screens as Voyager `Screen` implementations
- Use `ScreenModel` (Voyager's ViewModel alternative) for state management
- Bottom navigation with `Navigator` for tab switching
- Side panel navigation on large screens using adaptive layouts

### Mock Data Strategy

**Decision**: Kotlin DSL for mock data with simulated delays

**Rationale**:
- Type-safe DSL provides clean, readable test data
- Delay simulation validates loading states and async handling
- Easy to replace with real repository implementations later
- Allows realistic data volume testing (100+ channels, 1000+ programs)

**Implementation**:
```kotlin
// Example DSL
val mockChannels = channels {
    channel {
        id = "ct1"
        name = "ČT1"
        logoUrl = "..."
        category = ChannelCategory.NATIONAL
    }
    // ... more channels
}

// Simulated repository
class MockProgramRepository : ProgramRepository {
    override suspend fun getCurrentPrograms(): List<Program> {
        delay(500) // Simulate network/DB latency
        return mockPrograms.filter { it.isCurrentlyAiring }
    }
}
```

### Adaptive Layout Strategy

**Decision**: Material 3 Adaptive components with WindowSizeClass

**Rationale**:
- Material 3 provides `NavigationSuiteScaffold` for adaptive navigation
- `WindowSizeClass` (Compact, Medium, Expanded) determines layout
- Compose Multiplatform supports adaptive layouts across platforms
- Side panel for details on Medium/Expanded screens
- Bottom nav on Compact screens

**Implementation**:
- Compact (phone): Bottom navigation, full-screen detail
- Medium (tablet portrait, foldable): Navigation rail, side panel for detail
- Expanded (tablet landscape, desktop): Navigation rail, persistent side panel, multi-pane views

### Phase 0 Deliverables

- `research.md`: Consolidate all decisions above
- Technology choices documented with rationale
- All NEEDS CLARIFICATION items resolved

## Phase 1: Data Model & Contracts

### Core Entities (from spec)

**Channel**
- `id: String` (primary key)
- `name: String`
- `logoUrl: String`
- `channelNumber: Int?` (optional, user-assigned)
- `category: ChannelCategory` (enum: NATIONAL, REGIONAL, SPORTS, MOVIES, KIDS, etc.)
- `broadcastTechnology: BroadcastTechnology` (enum: TERRESTRIAL, SATELLITE, CABLE, STREAMING)
- `operatorId: String?` (for operator-based auto-selection)

**Program**
- `id: String` (primary key)
- `title: String`
- `description: String`
- `genre: String`
- `duration: Int` (minutes)
- `startTime: Instant`
- `endTime: Instant`
- `ageRating: String?` (e.g., "12+", "18+")
- `type: ProgramType` (enum: MOVIE, SERIES, SPORTS, DOCUMENTARY, KIDS, NEWS, etc.)
- `channelId: String` (foreign key)
- `imageUrl: String?`

**Episode** (extends Program for series)
- `seasonNumber: Int`
- `episodeNumber: Int`
- `seriesTitle: String`
- `seriesId: String`

**FavoriteProgram**
- `id: String`
- `programId: String`
- `userId: String?` (null for anonymous)
- `notificationsEnabled: Boolean`
- `reminderTimes: List<Int>` (minutes before, e.g., [30, 5])
- `createdAt: Instant`

**FavoriteChannelList**
- `id: String`
- `name: String` (e.g., "Sports", "Movies & Series")
- `userId: String?`
- `channelIds: List<String>` (ordered)
- `isActive: Boolean`

**UserPreferences**
- `userId: String?`
- `themeMode: ThemeMode` (enum: LIGHT, DARK, SYSTEM)
- `customColors: CustomColors?`
- `navigationItems: List<NavigationItem>` (ordered)
- `favoriteGenres: List<String>`
- `favoriteSports: List<String>`
- `language: String` (ISO code)

### Room Database Schema

```kotlin
@Database(
    entities = [
        ChannelEntity::class,
        ProgramEntity::class,
        FavoriteProgramEntity::class,
        FavoriteChannelListEntity::class,
        UserPreferencesEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun programDao(): ProgramDao
    abstract fun favoriteProgramDao(): FavoriteProgramDao
    abstract fun favoriteChannelListDao(): FavoriteChannelListDao
    abstract fun userPreferencesDao(): UserPreferencesDao
}
```

### API Contracts (Future Integration)

**EPG Data Provider** (OpenAPI spec in `contracts/epg-api.yaml`):
- `GET /channels` - List all channels
- `GET /programs?channelId={id}&startTime={iso}&endTime={iso}` - Get programs for time range
- `GET /programs/{id}` - Get program details
- `GET /programs/{id}/similar` - Get similar programs
- `GET /cast/{id}/programs` - Get programs by cast member

**ČSFD/IMDB Integration**:
- Direct links to external sites (no API needed for Phase 1)
- Future: Scraping or official API if available

### Phase 1 Deliverables

- `data-model.md`: Complete entity definitions with relationships
- `contracts/epg-api.yaml`: OpenAPI spec for future EPG provider integration
- `quickstart.md`: Developer guide for setting up and running the prototype
- Update `.github/copilot-instructions.md` with project-specific context

## Implementation Iterations (Phase 1 Prototype)

### Iteration 1: Scaffold & Navigation (Week 1)

**Goal**: Basic app structure with navigation

**Tasks**:
1. Set up KMP project structure (already exists, verify configuration)
2. Add dependencies: Metro DI, Voyager, Kermit, Coil
3. Configure Material 3 theme with adaptive support
4. Create `AdaptiveScaffold` component with:
   - Bottom navigation (Compact)
   - Navigation rail (Medium/Expanded)
   - Top app bar with settings and account icons
5. Define navigation structure with Voyager screens:
   - HomeScreen (Now on TV)
   - FavoritesScreen
   - EpgScreen
   - ListScreen
   - SettingsScreen
6. Implement basic screen placeholders (just text showing screen name)
7. Set up Metro DI modules (AppModule, DataModule, DomainModule)
8. Configure Kermit logging

**Deliverables**:
- App launches on all platforms
- Bottom nav/nav rail works
- Can navigate between placeholder screens
- Theme switching works (light/dark/system)

### Iteration 2: Home Screen - Now on TV (Week 2)

**Goal**: P1 - Current program view with adaptive layout

**Tasks**:
1. Create `Channel` and `Program` domain models
2. Implement `MockChannelRepository` and `MockProgramRepository` with DSL
3. Create `GetCurrentProgramsUseCase`
4. Implement `HomeScreen` and `HomeScreenModel`:
   - LazyColumn of current programs
   - Channel logo + program title + time remaining
   - Pull-to-refresh
   - Loading state
   - Empty state (no favorites set)
5. Create `ProgramCard` component (reusable)
6. Create `ChannelLogo` component with Coil integration
7. Adaptive layout:
   - Compact: Single column list
   - Medium/Expanded: 2-3 column grid
8. Click program → navigate to detail (placeholder for now)

**Deliverables**:
- Home screen shows current programs for mock channels
- Responsive grid layout on tablets/desktop
- Loading states visible
- Smooth scrolling (60fps target)

### Iteration 3: Favorites Management (Week 3)

**Goal**: P1 - Select and organize favorite channels

**Tasks**:
1. Create `FavoriteChannelList` model
2. Implement `GetFavoriteChannelsUseCase`, `UpdateFavoriteChannelsUseCase`
3. Implement `FavoritesScreen` and `FavoritesScreenModel`:
   - List of all available channels (grouped by category)
   - Checkboxes for selection
   - Drag-to-reorder functionality (DragAndDrop modifier)
   - Multiple lists support (tabs: "All Channels", "Sports", "Movies", etc.)
   - Search/filter by name
4. Create `ChannelListItem` component
5. Adaptive layout:
   - Compact: Full-screen list
   - Medium/Expanded: 2-column with categories on left, channels on right
6. Persist selections (Room database)
7. Update HomeScreen to filter by favorites

**Deliverables**:
- Can select/deselect channels
- Drag-to-reorder works
- Multiple favorite lists
- Changes persist across app restarts
- Home screen updates based on favorites

### Iteration 4: EPG Grid View (Week 4)

**Goal**: P2 - Traditional EPG grid with adaptive side panel

**Tasks**:
1. Integrate/fork `programguide` library
2. Create `GetEpgDataUseCase` (7 days of data)
3. Implement `EpgScreen` and `EpgScreenModel`:
   - Time slots (horizontal) vs channels (vertical)
   - Scrollable in both directions
   - Current time indicator
   - Tap program → show detail
4. Adapt `programguide` for Material 3 theming
5. Adaptive layout:
   - Compact: Full-screen grid, detail opens new screen
   - Medium: Grid with bottom sheet detail
   - Expanded: Grid (70%) + persistent side panel detail (30%)
6. Performance optimization:
   - Virtual scrolling
   - Load data incrementally
   - Smooth 60fps scrolling

**Deliverables**:
- EPG grid displays 7 days of programs
- Smooth scrolling
- Detail opens in side panel on large screens
- Current time highlighted
- Can browse future programming

### Iteration 5: Chronological List & Detail Screen (Week 5)

**Goal**: P2 - List view and detailed program information

**Tasks**:
1. Implement `ListScreen` and `ListScreenModel`:
   - Chronological list of all programs from favorites
   - Day separators
   - Filter by channel (tap logo)
   - Infinite scroll (load more days)
2. Adaptive layout:
   - Compact: Single day visible
   - Medium/Expanded: 2-3 days visible simultaneously
3. Implement `DetailScreen` and `DetailScreenModel`:
   - Program title, description, genre, duration, age rating
   - Broadcast times across all channels
   - Cast and crew (with photos)
   - ČSFD/IMDB ratings with links
   - Similar programs section
   - "Add to Favorites" button
4. Create `CastMemberCard`, `SimilarProgramCard` components
5. Adaptive detail:
   - Compact: Full-screen
   - Medium/Expanded: Side panel (shared with EPG)

**Deliverables**:
- List view shows programs chronologically
- Can filter by channel
- Detail screen shows complete information
- External links work
- Detail integrates with EPG and List screens

### Iteration 6: Settings & Polish (Week 6)

**Goal**: Complete Phase 1 prototype with settings and refinements

**Tasks**:
1. Implement `SettingsScreen`:
   - Theme selection (Light/Dark/System)
   - Custom color picker (Material 3 dynamic colors)
   - Navigation customization (show/hide items, reorder)
   - Language selection
   - About section
2. Create `UserPreferencesRepository` (local storage)
3. Polish all screens:
   - Error states
   - Empty states
   - Loading indicators
   - Animations and transitions
   - Accessibility (content descriptions, semantic properties)
4. Performance audit:
   - Measure frame rates
   - Optimize heavy lists
   - Image caching verification
5. Cross-platform testing:
   - Android (phone, tablet, foldable)
   - iOS (iPhone, iPad)
   - Desktop (Windows, macOS, Linux)
   - Web (Chrome, Firefox, Safari)
6. Documentation:
   - Update quickstart.md
   - Code comments
   - README

**Deliverables**:
- Settings screen fully functional
- All screens polished and performant
- Accessibility improvements
- Tested on all target platforms
- Documentation updated

## Deferred to Phase 2

The following features are **excluded from Phase 1 prototype**:

- Widgets (Android/iOS home screen)
- Push notifications
- Local notifications / exact alarms
- App shortcuts
- Real API integration
- Account creation / authentication
- Settings synchronization
- Favorite programs with notifications (P3)
- Content recommendations (P3)
- Advanced filtering (P4)
- Search functionality (P4)

These will be planned in Phase 2 after prototype validation.

## Phase 1 Success Criteria

- ✅ App launches and runs on Android, iOS, web, desktop
- ✅ Bottom navigation / nav rail works responsively
- ✅ Home screen shows current programs (mocked)
- ✅ Can manage favorite channels with persistence
- ✅ EPG grid displays 7 days of programming
- ✅ List view shows chronological programs
- ✅ Detail screen shows complete program information
- ✅ Settings screen allows theme and navigation customization
- ✅ Adaptive layouts work on phones, tablets, foldables, desktop
- ✅ Side panel for details on medium/expanded screens
- ✅ 60fps scrolling performance
- ✅ WCAG AA accessibility compliance
- ✅ All screens polished with proper loading/error/empty states

## Next Steps

After Phase 1 completion:

1. **User Validation**: Demo prototype to stakeholders, gather feedback
2. **Spec Refinement**: Update spec based on UX learnings
3. **Phase 2 Planning**: Run `/speckit.tasks` to generate detailed task breakdown for Phase 2 (real API integration, notifications, widgets, P3/P4 features)
4. **TDD Implementation**: Write comprehensive tests before implementing real functionality

**Ready for**: `/speckit.tasks` command to generate Phase 1 iteration task breakdowns
