# Tasks: TV Program Guide Application

**Input**: Design documents from `/specs/001-tv-guide-app/`  
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/ ✅

**Tests**: Phase 1 is a prototype with mocked data. Test-first development is DEFERRED to Phase 2 per constitution approval. No test tasks are included in this phase.

**Organization**: Tasks are grouped by iteration (from plan.md) which maps to user stories from spec.md.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

## Path Conventions

Kotlin Multiplatform project structure:
- **Common code**: `composeApp/src/commonMain/kotlin/`
- **Android**: `composeApp/src/androidMain/kotlin/`
- **iOS**: `composeApp/src/iosMain/kotlin/`
- **Desktop**: `composeApp/src/jvmMain/kotlin/`
- **Web**: `composeApp/src/wasmJsMain/kotlin/`
- **Resources**: `composeApp/src/commonMain/resources/`

---

## Phase 1: Setup & Project Initialization

**Purpose**: Verify KMP project structure and configure dependencies

- [ ] T001 Verify existing KMP project structure matches plan.md layout
- [ ] T002 Add Metro DI dependency in composeApp/build.gradle.kts
- [ ] T003 [P] Add Voyager navigation dependency in composeApp/build.gradle.kts
- [ ] T004 [P] Add Kermit logging dependency in composeApp/build.gradle.kts
- [ ] T005 [P] Add Coil 3 image loading dependency in composeApp/build.gradle.kts
- [ ] T006 [P] Add Room database dependency in composeApp/build.gradle.kts
- [ ] T007 [P] Add kotlinx-datetime dependency in composeApp/build.gradle.kts
- [ ] T008 Sync Gradle and verify all platforms build successfully
- [ ] T009 Create package structure in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/

---

## Phase 2: Foundational Infrastructure (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story implementation

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T010 Create Material 3 theme in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/theme/Color.kt
- [ ] T011 [P] Create Typography definitions in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/theme/Typography.kt
- [ ] T012 [P] Create Theme.kt with light/dark/system theme support in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/theme/Theme.kt
- [ ] T013 Setup Kermit logger wrapper in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/util/Logger.kt
- [ ] T014 Create Metro DI AppModule in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/di/AppModule.kt
- [ ] T015 [P] Create Metro DI DataModule in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/di/DataModule.kt
- [ ] T016 [P] Create Metro DI DomainModule in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/di/DomainModule.kt
- [ ] T017 Create domain model Channel in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/model/Channel.kt
- [ ] T018 [P] Create domain model Program in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/model/Program.kt
- [ ] T019 [P] Create domain model Episode in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/model/Episode.kt
- [ ] T020 [P] Create domain model FavoriteProgram in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/model/FavoriteProgram.kt
- [ ] T021 [P] Create domain model FavoriteChannelList in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/model/FavoriteChannelList.kt
- [ ] T022 [P] Create domain model UserPreferences in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/model/UserPreferences.kt
- [ ] T023 Create ChannelRepository interface in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/repository/ChannelRepository.kt
- [ ] T024 [P] Create ProgramRepository interface in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/repository/ProgramRepository.kt
- [ ] T025 [P] Create UserPreferencesRepository interface in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/repository/UserPreferencesRepository.kt
- [ ] T026 Create mock data DSL builder in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/data/mock/MockDataDsl.kt
- [ ] T027 Create delay simulator utility in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/data/mock/DelaySimulator.kt
- [ ] T028 Create mock channel data (100+ channels) in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/data/mock/MockChannels.kt
- [ ] T029 Create mock program data (1000+ programs) in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/data/mock/MockPrograms.kt
- [ ] T030 Implement MockChannelRepository with simulated delays in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/data/repository/MockChannelRepository.kt
- [ ] T031 [P] Implement MockProgramRepository with simulated delays in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/data/repository/MockProgramRepository.kt
- [ ] T032 [P] Implement LocalUserPreferencesRepository in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/data/repository/LocalUserPreferencesRepository.kt
- [ ] T033 Register all repositories in DataModule with Metro DI
- [ ] T034 Create extension functions in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/util/Extensions.kt

**Checkpoint**: Foundation ready - iteration implementation can now begin

---

## Phase 3: Iteration 1 - Scaffold & Navigation (Week 1)

**Goal**: Basic app structure with adaptive navigation across all platforms

**Independent Test**: Launch app on all platforms, navigate between screens, verify bottom nav (phone) and nav rail (tablet/desktop) work correctly, theme switching works

**Maps to**: Infrastructure for User Stories 1-12

- [ ] T035 Create AdaptiveScaffold component in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/components/AdaptiveScaffold.kt
- [ ] T036 Create Voyager Tab definitions (HomeTab, FavoritesTab, EpgTab, ListTab, SettingsTab) in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/navigation/Tabs.kt
- [ ] T037 [P] Create HomeScreen placeholder in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/home/HomeScreen.kt
- [ ] T038 [P] Create FavoritesScreen placeholder in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/favorites/FavoritesScreen.kt
- [ ] T039 [P] Create EpgScreen placeholder in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/epg/EpgScreen.kt
- [ ] T040 [P] Create ListScreen placeholder in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/list/ListScreen.kt
- [ ] T041 [P] Create SettingsScreen placeholder in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/settings/SettingsScreen.kt
- [ ] T042 Implement TabNavigator with NavigationSuiteScaffold in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/App.kt
- [ ] T043 Configure WindowSizeClass-based navigation type switching (bottom bar vs nav rail) in AdaptiveScaffold
- [ ] T044 Implement theme state management in App.kt with remember/mutableStateOf
- [ ] T045 Add theme toggle button to SettingsScreen placeholder
- [ ] T046 Create Android MainActivity in composeApp/src/androidMain/kotlin/cz/myapp/tvguide/MainActivity.kt
- [ ] T047 [P] Create iOS app entry point in composeApp/src/iosMain/kotlin/cz/myapp/tvguide/MainViewController.kt
- [ ] T048 [P] Create Desktop app entry point in composeApp/src/jvmMain/kotlin/cz/myapp/tvguide/main.kt
- [ ] T049 [P] Create Web app entry point in composeApp/src/wasmJsMain/kotlin/cz/myapp/tvguide/main.kt
- [ ] T050 Test app launch on all platforms (Android, iOS, Desktop, Web)
- [ ] T051 Verify navigation works on all platforms
- [ ] T052 Verify theme switching works on all platforms

**Checkpoint**: Navigation scaffold complete, ready for screen implementations

---

## Phase 4: Iteration 2 - Home Screen "Now on TV" (Week 2)

**Goal**: P1 User Story 1 - Current program view with adaptive layout

**Independent Test**: Launch app, see current programs from mock channels with logos and time remaining, pull to refresh, tap program to navigate to detail placeholder, verify grid layout on tablets

**Maps to**: User Story 1 - Current TV Program View

- [ ] T053 [US1] Create GetCurrentProgramsUseCase in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/usecase/GetCurrentProgramsUseCase.kt
- [ ] T054 [US1] Create GetFavoriteChannelsUseCase in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/usecase/GetFavoriteChannelsUseCase.kt
- [ ] T055 [US1] Register use cases in DomainModule
- [ ] T056 [US1] Create HomeScreenModel with Voyager ScreenModel in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/home/HomeScreenModel.kt
- [ ] T057 [US1] Implement HomeScreen UI with LazyVerticalGrid in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/home/HomeScreen.kt
- [ ] T058 [US1] Create ProgramCard reusable component in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/components/ProgramCard.kt
- [ ] T059 [US1] Create ChannelLogo component with Coil AsyncImage in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/components/ChannelLogo.kt
- [ ] T060 [US1] Implement pull-to-refresh in HomeScreen
- [ ] T061 [US1] Add loading state UI (CircularProgressIndicator) in HomeScreen
- [ ] T062 [US1] Add empty state UI (no favorites configured) in HomeScreen
- [ ] T063 [US1] Implement adaptive layout: single column (Compact), 2-3 column grid (Medium/Expanded)
- [ ] T064 [US1] Add program click navigation to DetailScreen placeholder
- [ ] T065 [US1] Implement auto-refresh when current program ends
- [ ] T066 [US1] Test on Android device/emulator
- [ ] T067 [US1] Test on iOS simulator
- [ ] T068 [US1] Test on Desktop
- [ ] T069 [US1] Test on Web browser
- [ ] T070 [US1] Verify 60fps scrolling performance

**Checkpoint**: User Story 1 complete - users can see current TV programs

---

## Phase 5: Iteration 3 - Favorites Management (Week 3)

**Goal**: P1 User Story 2 - Select and organize favorite channels

**Independent Test**: Open Favorites screen, select/deselect channels, reorder via drag-and-drop, create multiple lists (Sports, Movies), verify changes persist and affect Home screen

**Maps to**: User Story 2 - Favorite Channels Management

- [ ] T071 [US2] Create GetAllChannelsUseCase in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/usecase/GetAllChannelsUseCase.kt
- [ ] T072 [US2] Create UpdateFavoriteChannelsUseCase in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/usecase/UpdateFavoriteChannelsUseCase.kt
- [ ] T073 [US2] Create CreateChannelListUseCase in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/usecase/CreateChannelListUseCase.kt
- [ ] T074 [US2] Register use cases in DomainModule
- [ ] T075 [US2] Create FavoritesScreenModel in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/favorites/FavoritesScreenModel.kt
- [ ] T076 [US2] Implement FavoritesScreen UI with channel list in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/favorites/FavoritesScreen.kt
- [ ] T077 [US2] Create ChannelListItem component with checkbox in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/components/ChannelListItem.kt
- [ ] T078 [US2] Implement channel grouping by category (National, Regional, Sports, etc.)
- [ ] T079 [US2] Implement drag-and-drop reordering with Compose Modifier
- [ ] T080 [US2] Add search/filter functionality in FavoritesScreen
- [ ] T081 [US2] Implement multiple channel lists with tabs (All Channels, Sports, Movies)
- [ ] T082 [US2] Add create new list dialog
- [ ] T083 [US2] Implement list switching persistence
- [ ] T084 [US2] Adaptive layout: full-screen list (Compact), side-by-side preview (Medium/Expanded)
- [ ] T085 [US2] Test channel selection persists across app restarts
- [ ] T086 [US2] Test reordering affects HomeScreen display order
- [ ] T087 [US2] Test on all platforms

**Checkpoint**: User Story 2 complete - users can manage favorite channels

---

## Phase 6: Iteration 4 - EPG Grid View (Week 4)

**Goal**: P2 User Story 3 - Traditional EPG grid with time/channel axes

**Independent Test**: Open EPG screen, scroll horizontally (time) and vertically (channels), tap program to see details in side panel (large screens) or bottom sheet (small screens), verify 7-day data

**Maps to**: User Story 3 - EPG Grid View

- [ ] T088 [US3] Research programguide library KMP compatibility
- [ ] T089 [US3] Add programguide library dependency or create fork if needed
- [ ] T090 [US3] Create GetEpgDataUseCase in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/usecase/GetEpgDataUseCase.kt
- [ ] T091 [US3] Register use case in DomainModule
- [ ] T092 [US3] Create EpgScreenModel in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/epg/EpgScreenModel.kt
- [ ] T093 [US3] Implement EpgScreen with programguide grid in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/epg/EpgScreen.kt
- [ ] T094 [US3] Create EpgProgramItem component (grid cell) in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/components/EpgProgramItem.kt
- [ ] T095 [US3] Implement horizontal time scrolling (current time to +7 days)
- [ ] T096 [US3] Implement vertical channel scrolling
- [ ] T097 [US3] Add current time indicator line in grid
- [ ] T098 [US3] Implement pinch-to-zoom for time scale adjustment
- [ ] T099 [US3] Add program tap handling with detail navigation
- [ ] T100 [US3] Implement adaptive detail display: ModalBottomSheet (Compact), side panel (Medium/Expanded)
- [ ] T101 [US3] Create ProgramDetailPanel component in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/components/ProgramDetailPanel.kt
- [ ] T102 [US3] Add loading state for EPG data
- [ ] T103 [US3] Test grid scrolling performance (60fps target)
- [ ] T104 [US3] Test on all platforms

**Checkpoint**: User Story 3 complete - users can browse EPG grid

---

## Phase 7: Iteration 5 - Chronological List & Detail (Week 5)

**Goal**: P2 User Stories 4 & 5 - List view and detailed program information

**Independent Test**: Switch to List view, see all programs chronologically, filter by channel, scroll to tomorrow's schedule. Tap program to see full details with cast, ratings, similar content, external links.

**Maps to**: User Story 4 - Chronological List View, User Story 5 - Program Details & Discovery

- [ ] T105 [US4] Create GetChronologicalProgramsUseCase in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/usecase/GetChronologicalProgramsUseCase.kt
- [ ] T106 [US4] Register use case in DomainModule
- [ ] T107 [US4] Create ListScreenModel in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/list/ListScreenModel.kt
- [ ] T108 [US4] Implement ListScreen UI with LazyColumn in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/list/ListScreen.kt
- [ ] T109 [US4] Create ProgramListItem component in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/components/ProgramListItem.kt
- [ ] T110 [US4] Implement channel logo tap to filter by single channel
- [ ] T111 [US4] Add "All Channels" filter toggle
- [ ] T112 [US4] Implement infinite scroll to next day's programs
- [ ] T113 [US4] Add day separator headers
- [ ] T114 [US4] Adaptive layout: multi-day view on tablets (2-3 columns)
- [ ] T115 [US5] Create CastMember and ProgramCast domain models in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/model/
- [ ] T116 [US5] Create GetProgramDetailsUseCase in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/usecase/GetProgramDetailsUseCase.kt
- [ ] T117 [US5] Create GetSimilarProgramsUseCase in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/usecase/GetSimilarProgramsUseCase.kt
- [ ] T118 [US5] Create GetProgramsByCastMemberUseCase in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/usecase/GetProgramsByCastMemberUseCase.kt
- [ ] T119 [US5] Register use cases in DomainModule
- [ ] T120 [US5] Create DetailScreenModel in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/detail/DetailScreenModel.kt
- [ ] T121 [US5] Implement DetailScreen UI in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/detail/DetailScreen.kt
- [ ] T122 [US5] Create CastMemberCard component in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/components/CastMemberCard.kt
- [ ] T123 [US5] Display program poster image with Coil
- [ ] T124 [US5] Display title, description, genre, duration, age rating
- [ ] T125 [US5] Display cast and crew section with photos
- [ ] T126 [US5] Add ČSFD and IMDB rating section with external links
- [ ] T127 [US5] Implement similar content recommendations section
- [ ] T128 [US5] Add broadcast schedule across all channels
- [ ] T129 [US5] Implement cast member tap navigation to their programs list
- [ ] T130 [US5] Add favorite program toggle button
- [ ] T131 [US5] Adaptive layout: scrollable column (Compact), two-column with sticky poster (Medium/Expanded)
- [ ] T132 [US4] [US5] Test on all platforms

**Checkpoint**: User Stories 4 & 5 complete - users can browse lists and discover content

---

## Phase 8: Iteration 6 - Settings & Polish (Week 6)

**Goal**: User Stories 10 (Theme customization) and final polish across all features

**Independent Test**: Open Settings, change theme (Light/Dark/System), verify theme applies across all screens, customize navigation items, test accessibility features

**Maps to**: User Story 10 - Theme & Navigation Customization, plus cross-cutting concerns

- [ ] T133 [US10] Create GetUserPreferencesUseCase in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/usecase/GetUserPreferencesUseCase.kt
- [ ] T134 [US10] Create UpdateUserPreferencesUseCase in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/domain/usecase/UpdateUserPreferencesUseCase.kt
- [ ] T135 [US10] Register use cases in DomainModule
- [ ] T136 [US10] Create SettingsScreenModel in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/settings/SettingsScreenModel.kt
- [ ] T137 [US10] Implement SettingsScreen UI in composeApp/src/commonMain/kotlin/cz/myapp/tvguide/presentation/screens/settings/SettingsScreen.kt
- [ ] T138 [US10] Add theme selection (Light/Dark/System Default)
- [ ] T139 [US10] Add custom theme color pickers (primary/accent)
- [ ] T140 [US10] Add navigation customization section
- [ ] T141 [US10] Implement navigation item reordering
- [ ] T142 [US10] Implement navigation item show/hide toggles
- [ ] T143 [US10] Add time format preference (12h/24h)
- [ ] T144 [US10] Add compact mode toggle
- [ ] T145 [US10] Persist all settings changes
- [ ] T146 Add accessibility improvements (content descriptions, semantic properties)
- [ ] T147 [P] Add loading error handling across all screens
- [ ] T148 [P] Add empty state handling across all screens
- [ ] T149 [P] Optimize mock data delays for realistic feel
- [ ] T150 [P] Verify Material 3 design compliance across all screens
- [ ] T151 Test deep linking from HomeScreen program click to DetailScreen
- [ ] T152 Test navigation state persistence across app restarts
- [ ] T153 Verify WCAG AA accessibility compliance
- [ ] T154 Performance testing: 60fps scrolling in all lists and grids
- [ ] T155 Performance testing: <3s app launch time
- [ ] T156 Performance testing: <1s program detail load time
- [ ] T157 Cross-platform testing: Android API 24+
- [ ] T158 Cross-platform testing: iOS 15+
- [ ] T159 Cross-platform testing: Desktop (Windows/macOS/Linux)
- [ ] T160 Cross-platform testing: Web (Chrome, Firefox, Safari)
- [ ] T161 Run quickstart.md validation on fresh setup

**Checkpoint**: Phase 1 prototype complete - all P1 and P2 user stories functional

---

## Deferred to Phase 2

The following user stories and features are explicitly deferred to Phase 2 (real API integration):

- **User Story 6** (P3): Favorite Programs & Notifications - requires real-time scheduling
- **User Story 7** (P3): Content Categories & Recommendations - requires usage history
- **User Story 8** (P4): Channel Numbering - lower priority
- **User Story 9** (P4): Advanced Filtering & Search - lower priority
- **User Story 11** (P4): Widgets & Quick Access - platform-specific, requires real data
- **User Story 12** (P4): Settings Sync & Backup - requires authentication and backend

**Deferred Infrastructure**:
- Room database implementation (Phase 1 uses in-memory mock only)
- Ktor HTTP client integration
- Real EPG API integration per contracts/epg-api.yaml
- Authentication and account system
- Push notification infrastructure
- Test-first development (TDD)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies - can start immediately
- **Phase 2 (Foundational)**: Depends on Phase 1 completion - BLOCKS all iterations
- **Phase 3 (Iteration 1)**: Depends on Phase 2 completion
- **Phase 4 (Iteration 2)**: Depends on Phase 3 completion (navigation scaffold required)
- **Phase 5 (Iteration 3)**: Depends on Phase 3 completion (can run parallel with Phase 4)
- **Phase 6 (Iteration 4)**: Depends on Phase 5 completion (uses favorites data)
- **Phase 7 (Iteration 5)**: Depends on Phase 3 completion (can run parallel with Phase 4-6)
- **Phase 8 (Iteration 6)**: Depends on all previous iterations (integrates everything)

### User Story Dependencies

- **US1 (Current Programs)**: Requires Phase 2 foundational models and repos
- **US2 (Favorites Management)**: Independent of US1, requires Phase 2
- **US3 (EPG Grid)**: Requires US2 (uses favorite channels)
- **US4 (Chronological List)**: Independent of US1-3, requires Phase 2
- **US5 (Program Details)**: Independent implementation, integrates with US1, US3, US4 for navigation
- **US10 (Settings)**: Independent implementation, applies to all screens

### Parallel Opportunities

**Within Phase 1 (Setup)**:
- Tasks T003-T007 (dependencies) can run in parallel

**Within Phase 2 (Foundational)**:
- Tasks T011-T012 (theme files) can run in parallel
- Tasks T015-T016 (DI modules) can run in parallel after T014
- Tasks T017-T022 (domain models) can run in parallel
- Tasks T023-T025 (repository interfaces) can run in parallel after models
- Tasks T030-T032 (repository implementations) can run in parallel

**Across Iterations**:
- Iteration 3 (Favorites) can start in parallel with Iteration 2 (Home Screen)
- Iteration 5 (List & Detail) can start in parallel with Iterations 2-4

**Within Iterations**:
- All placeholder screen creations (T037-T041) can run in parallel
- All platform entry points (T046-T049) can run in parallel
- All use case registrations can run in parallel after use case creation
- All model creations within an iteration can run in parallel

---

## Parallel Example: Iteration 2 (Home Screen)

```bash
# Launch all use cases together:
Task T053: "Create GetCurrentProgramsUseCase"
Task T054: "Create GetFavoriteChannelsUseCase"

# Launch all components together (after ScreenModel):
Task T058: "Create ProgramCard component"
Task T059: "Create ChannelLogo component"

# Launch all platform tests together (after implementation):
Task T066: "Test on Android"
Task T067: "Test on iOS"
Task T068: "Test on Desktop"
Task T069: "Test on Web"
```

---

## Implementation Strategy

### MVP First (Iterations 1-2 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL)
3. Complete Phase 3: Iteration 1 (Navigation scaffold)
4. Complete Phase 4: Iteration 2 (Home Screen - US1)
5. **STOP and VALIDATE**: Test US1 independently on all platforms
6. Demo to stakeholders

### Recommended Approach (P1 User Stories)

1. Complete Setup + Foundational (Phases 1-2)
2. Complete Iteration 1 (Navigation) - Foundation for all screens
3. Complete Iteration 2 (Home Screen - US1)
4. Complete Iteration 3 (Favorites - US2)
5. **MILESTONE**: P1 complete - Deploy/Demo
6. Continue with P2 user stories (Iterations 4-5)

### Full Phase 1 Prototype

1. Phases 1-2: Setup + Foundational
2. Phases 3-8: All 6 iterations
3. **DELIVERABLE**: Functional prototype with US1-US5, US10
4. Ready for stakeholder UX validation
5. Then proceed to Phase 2: TDD + Real API integration

### Parallel Team Strategy

With 3 developers after Foundational phase completes:

- **Developer A**: Iteration 1 (Navigation) → Iteration 2 (Home)
- **Developer B**: Iteration 3 (Favorites) → Iteration 4 (EPG)
- **Developer C**: Iteration 5 (List & Detail) → Iteration 6 (Settings)

Integration points coordinated at end of each week.

---

## Summary Statistics

- **Total Tasks**: 161
- **Setup Phase**: 9 tasks
- **Foundational Phase**: 25 tasks (BLOCKING)
- **Iteration 1 (Navigation)**: 18 tasks
- **Iteration 2 (Home - US1)**: 18 tasks
- **Iteration 3 (Favorites - US2)**: 17 tasks
- **Iteration 4 (EPG - US3)**: 17 tasks
- **Iteration 5 (List & Detail - US4, US5)**: 28 tasks
- **Iteration 6 (Settings & Polish - US10)**: 29 tasks

**User Story Coverage**:
- ✅ US1 (P1): Current TV Programs - 18 tasks (Iteration 2)
- ✅ US2 (P1): Favorite Channels - 17 tasks (Iteration 3)
- ✅ US3 (P2): EPG Grid View - 17 tasks (Iteration 4)
- ✅ US4 (P2): Chronological List - 14 tasks (Iteration 5)
- ✅ US5 (P2): Program Details - 14 tasks (Iteration 5)
- ✅ US10 (P4): Theme Customization - 13 tasks (Iteration 6)
- ⏸️ US6 (P3): Favorite Programs & Notifications - Deferred to Phase 2
- ⏸️ US7 (P3): Categories & Recommendations - Deferred to Phase 2
- ⏸️ US8 (P4): Channel Numbering - Deferred to Phase 2
- ⏸️ US9 (P4): Advanced Filtering - Deferred to Phase 2
- ⏸️ US11 (P4): Widgets - Deferred to Phase 2
- ⏸️ US12 (P4): Settings Sync - Deferred to Phase 2

**Parallel Opportunities**:
- 45+ tasks marked [P] for parallel execution
- Multiple iterations can run in parallel (Iterations 2-5)
- All platform testing can run in parallel

**Suggested MVP Scope**:
- Phases 1-2 + Iteration 1-2 (Setup + Foundational + Navigation + Home Screen)
- Delivers User Story 1 independently
- ~52 tasks total
- Estimated 2 weeks with 1 developer

**Notes**:
- Tests are DEFERRED per constitution approval (Phase 1 is prototype)
- All tasks include exact file paths for LLM execution
- Each iteration delivers independently testable value
- Phase 1 focuses on UX validation, Phase 2 will add TDD + real API
