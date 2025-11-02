# Feature Specification: TV Program Guide Application

**Feature Branch**: `001-tv-guide-app`  
**Created**: 2025-11-02  
**Status**: Draft  
**Input**: User description: "Aplikace: Televizní program, pro široké publikum uživatelů, od mladých po staré, od anoušku telenovel, až po sportovní fanoušky, od uživatelů se základními požadavky až po verné fanušky cenící jedinéčné funkce aplikace, od sledovatelů pozemního vysílání zdarma až po uživatele s online služeb se zpětným sledováním."

## Clarifications

### Session 2025-11-02

- Q: User Data Retention & Privacy Policy - How long should user data (favorites, preferences, viewing history) be retained for anonymous vs logged-in users? → A: Anonymous users: only essential data (favorite channels, favorite programs) retained, deleted after 180 days of inactivity. Logged-in users: indefinite retention until account deletion, or after 2 years of inactivity with warning emails sent before deletion.
- Q: Authentication & Account Security - What authentication security requirements should be implemented for user accounts? → A: Email/password with minimum 8 characters (mixed case + numbers required), OAuth2 for social login (Google, Apple), password hashing with bcrypt/Argon2. No 2FA required as app doesn't handle sensitive personal data.
- Q: EPG Data Source Failure Handling - What should happen when the primary EPG data source fails or returns incomplete data? → A: Fallback to cached data (up to 24 hours old) with clear "Data may be outdated" indicator displayed to users.
- Q: Settings Sync Conflict Resolution - How should conflicts be resolved when a user modifies settings on multiple devices while offline? → A: Hybrid approach - merge non-conflicting changes (e.g., favorite programs added on different devices are combined), use last-write-wins with timestamp for conflicting setting changes (e.g., theme preference modified on both devices).
- Q: Notification Delivery Strategy - How should program reminder notifications be scheduled and delivered? → A: Primary method: local device scheduling (Android exact alarms, iOS local notifications) for offline reliability. Secondary: push notifications as backup if local scheduling fails, and to trigger EPG data refresh when program schedules change.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Current TV Program View (Priority: P1)

As a TV viewer, I want to see what's currently playing on my favorite channels so I can quickly decide what to watch without switching through channels.

**Why this priority**: This is the core MVP feature - users need to see current programming immediately. Without this, the app has no basic value. This addresses the most common use case: "What's on TV right now?"

**Independent Test**: Can be fully tested by launching the app and verifying current programs are displayed for all channels. Delivers immediate value by showing real-time TV schedule.

**Acceptance Scenarios**:

1. **Given** the app is launched, **When** I navigate to "Now on TV" section, **Then** I see current programs for all my favorite channels with program titles, channel logos, and time remaining
2. **Given** I'm viewing current programs, **When** the current program ends, **Then** the display automatically updates to show the next program
3. **Given** I'm viewing "Now on TV", **When** I see a program title, **Then** I can tap it to see full program details

---

### User Story 2 - Favorite Channels Management (Priority: P1)

As a user, I want to select and organize my favorite channels so I only see content relevant to my viewing preferences.

**Why this priority**: Essential for personalization and usability. Users shouldn't have to scroll through hundreds of channels they never watch. This is required before P1 Story 1 can be truly useful.

**Independent Test**: Can be tested by adding/removing channels from favorites, reordering them, and verifying the changes persist across app restarts. Delivers value by personalizing the viewing experience.

**Acceptance Scenarios**:

1. **Given** I'm setting up the app for the first time, **When** I'm prompted to select favorite channels, **Then** I can choose from all available channels organized by categories (national, regional, sports, movies, kids, etc.)
2. **Given** I have selected favorite channels, **When** I long-press and drag a channel, **Then** I can reorder my favorites list
3. **Given** I want different channel sets for different moods, **When** I create a new favorites list (e.g., "Sports", "Movies & Series"), **Then** I can switch between these lists and each maintains its own channel selection and ordering
4. **Given** I'm in a specific location, **When** I choose "Auto-detect by location", **Then** the app suggests relevant regional channels based on my location
5. **Given** I have a TV service provider, **When** I select my operator from the list, **Then** the app pre-fills my favorites with channels from that operator's package

---

### User Story 3 - EPG Grid View (Priority: P2)

As a user who likes to browse TV schedules visually, I want to see a traditional EPG grid (time horizontally, channels vertically) so I can easily compare what's on across multiple channels at specific times.

**Why this priority**: Second-tier feature that power users expect. Provides comprehensive overview but not essential for basic usage.

**Independent Test**: Can be tested by navigating to the EPG view and verifying the grid displays correctly with scrolling in both directions, time slots are accurate, and program information is complete.

**Acceptance Scenarios**:

1. **Given** I open the EPG grid, **When** the view loads, **Then** I see my favorite channels listed vertically with time slots shown horizontally, starting from the current time
2. **Given** I'm viewing the EPG grid, **When** I scroll horizontally, **Then** I can browse future programming up to 7 days ahead
3. **Given** I'm viewing the EPG grid, **When** I scroll vertically, **Then** I can see all my favorite channels
4. **Given** I see a program in the grid, **When** I tap on it, **Then** I see the full program details
5. **Given** I'm viewing programs at a specific time, **When** I pinch to zoom, **Then** the time scale adjusts to show more or fewer hours on screen

---

### User Story 4 - Chronological List View (Priority: P2)

As a user who prefers a simple list format, I want to see all programs from all my favorite channels in chronological order so I can quickly scan upcoming shows across all channels.

**Why this priority**: Alternative viewing mode for users who prefer linear browsing over grid view. Important for accessibility and different user preferences.

**Independent Test**: Can be tested by switching to list view and verifying programs from all channels appear in time order, with filtering options working correctly.

**Acceptance Scenarios**:

1. **Given** I switch to list view, **When** the view loads, **Then** I see all programs from my favorite channels sorted chronologically, with channel logos visible next to each program
2. **Given** I'm viewing the chronological list, **When** I tap on a channel logo, **Then** the list filters to show only programs from that specific channel
3. **Given** I'm viewing filtered content, **When** I tap "All Channels" or the active filter, **Then** the list returns to showing all channels
4. **Given** I'm scrolling through the list, **When** I reach the end of today's programs, **Then** I can continue scrolling to see tomorrow's schedule

---

### User Story 5 - Program Details & Discovery (Priority: P2)

As a movie/series enthusiast, I want detailed information about programs including cast, ratings, and similar content recommendations so I can discover new content and make informed viewing decisions.

**Why this priority**: Enhances the basic guide functionality with rich content discovery. Differentiates the app from basic TV guides.

**Independent Test**: Can be tested by opening any program details and verifying all information displays correctly, external links work, and recommendations are relevant.

**Acceptance Scenarios**:

1. **Given** I tap on any program, **When** the details page opens, **Then** I see program title, description, genre, duration, age rating, and broadcast times across all channels (including non-favorites)
2. **Given** I'm viewing a movie or series, **When** I scroll down, **Then** I see cast and crew information with photos, director, writers, and main actors
3. **Given** I'm viewing program details, **When** I see the ratings section, **Then** I can view scores from ČSFD and IMDB with direct links to those websites
4. **Given** I'm viewing a movie, **When** I scroll to "Similar Content", **Then** I see recommendations for similar movies/series based on genre, cast, or director
5. **Given** I'm viewing cast information, **When** I tap on an actor or director name, **Then** I see a list of other programs featuring that person, with broadcast times
6. **Given** I'm viewing a program that airs on multiple channels, **When** I check broadcast information, **Then** I see all upcoming airings across all channels (favorites and non-favorites), sorted by time

---

### User Story 6 - Favorite Programs & Notifications (Priority: P3)

As a dedicated viewer of specific shows, I want to mark programs as favorites and receive notifications before they air so I never miss my favorite content.

**Why this priority**: Valuable for user retention but not essential for initial adoption. Users first need to discover content (P1-P2) before they can favorite it.

**Independent Test**: Can be tested by favoriting programs, setting notifications, and verifying alerts appear at the correct times.

**Acceptance Scenarios**:

1. **Given** I'm viewing any program, **When** I tap the "Favorite" icon, **Then** the program is added to my favorites list
2. **Given** I've favorited a program, **When** I access my favorites overview, **Then** I see all my favorite programs with next airing time and channel
3. **Given** I've favorited a program, **When** I enable notifications, **Then** I can set two reminder times (e.g., 30 minutes before and 5 minutes before)
4. **Given** a favorited program is about to air, **When** the notification time arrives, **Then** I receive a notification with program title, channel, and time
5. **Given** I favorited a series, **When** new episodes are scheduled, **Then** they automatically appear in my favorites with notifications enabled if I set them for the series

---

### User Story 7 - Content Categories & Recommendations (Priority: P3)

As a user interested in specific content types, I want curated sections for Sports, Movies, and Series with personalized recommendations so I can easily find content matching my interests.

**Why this priority**: Advanced feature that increases engagement but requires the foundation of basic guide functionality and some usage history.

**Independent Test**: Can be tested by navigating to each category section and verifying content is properly filtered and recommendations reflect user preferences.

**Acceptance Scenarios**:

1. **Given** I tap on "Sports" section, **When** the view loads, **Then** I see all upcoming sports programs from my favorite channels, organized by sport type
2. **Given** I tap on "Movies" section, **When** the view loads, **Then** I see all upcoming movies with posters, ratings, and broadcast times
3. **Given** I tap on "Series" section, **When** the view loads, **Then** I see all series episodes grouped by show, with episode numbers and descriptions
4. **Given** I've been using the app, **When** I view recommendations, **Then** I see suggested content based on my viewing history, favorites, and preferences
5. **Given** I want more control, **When** I access advanced settings, **Then** I can set preferred sports types, genres, and favorite actors/directors to improve recommendations

---

### User Story 8 - Channel Numbering & Organization (Priority: P4)

As a user accustomed to traditional TV, I want to assign channel numbers so I can reference channels the same way I do on my TV remote.

**Why this priority**: Nice-to-have feature for traditionalists but not critical for app functionality. Most users will navigate by channel names/logos.

**Independent Test**: Can be tested by assigning numbers to channels and verifying they display correctly throughout the app.

**Acceptance Scenarios**:

1. **Given** I'm managing favorite channels, **When** I enable channel numbering, **Then** I can manually assign numbers to each channel
2. **Given** I want automatic numbering, **When** I select "Auto-number by favorites order", **Then** channels are numbered 1, 2, 3, etc. based on my favorites list order
3. **Given** I have channel numbers assigned, **When** viewing any program list or grid, **Then** channel numbers appear alongside channel logos

---

### User Story 9 - Advanced Filtering & Search (Priority: P4)

As a power user, I want advanced filtering options so I can quickly find specific types of content (sport types, genres, time slots, etc.).

**Why this priority**: Power user feature that adds convenience but isn't necessary for core functionality.

**Independent Test**: Can be tested by applying various filters and verifying results match the filter criteria.

**Acceptance Scenarios**:

1. **Given** I'm viewing any program list, **When** I tap the filter icon, **Then** I can filter by program type (movie, series, sports, documentary, kids, etc.)
2. **Given** I'm viewing sports content, **When** I apply sport type filters, **Then** I can filter by specific sports (football, hockey, tennis, etc.)
3. **Given** I'm viewing movies/series, **When** I apply genre filters, **Then** I can filter by genres (action, comedy, drama, thriller, etc.)
4. **Given** I've set favorite genres and sports, **When** I view filtered content, **Then** my preferences are remembered and quick-filter buttons appear for my favorites
5. **Given** I'm browsing programs, **When** I use the search function, **Then** I can search by program title, actor name, or director name

---

### User Story 10 - Theme & Navigation Customization (Priority: P4)

As a user who values personalization, I want to customize the app's appearance and navigation so it matches my preferences and usage patterns.

**Why this priority**: Cosmetic and UX enhancements that improve satisfaction but don't affect core functionality.

**Independent Test**: Can be tested by changing theme and navigation settings and verifying the changes apply correctly across all screens.

**Acceptance Scenarios**:

1. **Given** I'm in settings, **When** I access theme options, **Then** I can choose between Light, Dark, and System Default (follows device settings)
2. **Given** I want a custom look, **When** I select custom theme, **Then** I can choose primary and accent colors
3. **Given** I'm in settings, **When** I access navigation customization, **Then** I can reorder, show, or hide navigation items (Now on TV, EPG, List, Sports, Movies, Series, Favorites, etc.)
4. **Given** I've customized navigation, **When** I use the app, **Then** only my selected items appear in the navigation bar in my chosen order

---

### User Story 11 - Widgets & Quick Access (Priority: P4)

As a mobile user, I want home screen widgets and app shortcuts so I can access key information without opening the full app.

**Why this priority**: Platform-specific enhancement that adds convenience but requires the core features to be built first.

**Independent Test**: Can be tested on each platform by adding widgets and shortcuts, then verifying they display correct information and deep-link properly.

**Acceptance Scenarios**:

1. **Given** I'm on Android/iOS, **When** I add the "Now on TV" widget, **Then** my home screen shows currently airing programs on my top favorite channels
2. **Given** I'm on Android/iOS, **When** I add the "Favorite Programs" widget, **Then** my home screen shows next airing times for my favorited shows
3. **Given** I'm on a platform supporting app shortcuts, **When** I long-press the app icon, **Then** I see shortcuts for "Now on TV", "Favorites", "Program Guide", and "Recommendations"
4. **Given** I tap a widget or shortcut, **When** the app opens, **Then** I'm taken directly to the relevant screen

---

### User Story 12 - Settings Sync & Backup (Priority: P4)

As a user who uses multiple devices, I want to optionally create an account so my settings, favorites, and preferences sync across all my devices.

**Why this priority**: Advanced feature for multi-device users. Not required for single-device usage, which will be most users initially.

**Independent Test**: Can be tested by creating an account, configuring settings on one device, then logging in on another device and verifying all settings synchronized correctly.

**Acceptance Scenarios**:

1. **Given** I want to sync my settings, **When** I choose to create an account, **Then** I can sign up using email or social login (Google, Apple)
2. **Given** I have an account, **When** I log in on a new device, **Then** all my favorite channels, programs, notification settings, and customizations are automatically restored
3. **Given** I'm using the app without an account, **When** I later create an account, **Then** my current settings are uploaded and preserved
4. **Given** I want to use the app privately, **When** I choose "Skip" on account creation, **Then** I can use all features with local-only storage (no sync)

---

### Edge Cases

- What happens when there's no internet connection and EPG data can't be updated? → System displays cached data (up to 24 hours old) with "Data may be outdated" indicator
- How does the app handle channels that have incomplete or missing program data? → System displays placeholder with channel logo and "Program information unavailable" message
- What happens when a user's favorite channel is discontinued or changes its broadcast ID?
- How does the app handle programs that run longer than scheduled (sports overtime, breaking news)?
- What happens when notification permissions are denied but user tries to enable program alerts?
- How does the app handle very long channel lists (100+ channels) in EPG grid view without performance degradation?
- What happens when external links (ČSFD, IMDB) are unavailable or the program isn't found in those databases?
- How does the app handle time zone changes when traveling?
- What happens when a widget tries to display data but the app hasn't fetched fresh EPG data? → Widget displays cached data with staleness indicator if data is older than 1 hour
- How does the app handle sync conflicts when user modifies the same setting on multiple devices? → Last-write-wins based on timestamp; for collections (favorite programs), changes are merged

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST display current program information for all selected favorite channels with program title, channel logo, start time, end time, and time remaining
- **FR-002**: System MUST allow users to select favorite channels from a complete list of available channels, organized by categories (national, regional, sports, entertainment, kids, etc.)
- **FR-003**: System MUST allow users to reorder their favorite channels via drag-and-drop or similar intuitive interaction
- **FR-004**: System MUST support multiple favorite channel lists (e.g., "Sports", "Movies & Series") with the ability to switch between them
- **FR-005**: System MUST provide automatic channel selection based on user's geographic location
- **FR-006**: System MUST provide automatic channel selection based on user's selected TV operator/provider
- **FR-007**: System MUST allow manual assignment of channel numbers to favorite channels
- **FR-008**: System MUST provide automatic channel numbering based on favorite channel order
- **FR-009**: System MUST display EPG data in a grid format with time on horizontal axis and channels on vertical axis, scrollable in both directions
- **FR-010**: System MUST display EPG data up to 7 days in advance
- **FR-011**: System MUST provide a chronological list view showing programs from all favorite channels sorted by time
- **FR-012**: System MUST allow filtering of chronological list view to show programs from a single channel when tapping that channel's logo
- **FR-013**: System MUST provide detailed program information including title, description, genre, duration, age rating, cast, crew, and broadcast schedule across all channels
- **FR-014**: System MUST display ratings from ČSFD and IMDB with clickable links to full reviews on those platforms
- **FR-015**: System MUST provide recommendations for similar programs based on genre, cast, or director
- **FR-016**: System MUST allow users to browse all programs featuring a specific actor or director
- **FR-017**: System MUST allow users to mark programs as favorites
- **FR-018**: System MUST provide an overview of all favorited programs with next airing information
- **FR-019**: System MUST allow users to set up to two notification reminders per favorited program
- **FR-020**: System MUST send notifications at the scheduled reminder times before program starts
- **FR-021**: System MUST provide dedicated sections for Sports, Movies, and Series content
- **FR-022**: System MUST organize sports programs by sport type
- **FR-023**: System MUST provide personalized content recommendations based on viewing history and preferences
- **FR-024**: System MUST allow users to set preferences for sports types, genres, and favorite creators to improve recommendations
- **FR-025**: System MUST support filtering programs by type (movie, series, sports, documentary, kids, etc.)
- **FR-026**: System MUST support filtering sports by specific sport types (football, hockey, tennis, etc.)
- **FR-027**: System MUST support filtering movies and series by genre
- **FR-028**: System MUST provide search functionality for programs, actors, and directors
- **FR-029**: System MUST support Light, Dark, and System Default (auto) theme modes
- **FR-030**: System MUST support custom themes with user-selected primary and accent colors
- **FR-031**: System MUST allow users to customize navigation bar items (reorder, show/hide)
- **FR-032**: System MUST provide home screen widgets showing current programs on favorite channels (platform-dependent: Android, iOS)
- **FR-033**: System MUST provide home screen widgets showing upcoming favorited programs (platform-dependent: Android, iOS)
- **FR-034**: System MUST provide app shortcuts for quick access to key screens: Now on TV, Favorites, Program Guide, Recommendations (platform-dependent)
- **FR-035**: System MUST support optional user account creation for settings synchronization
- **FR-036**: System MUST synchronize all user settings, favorites, and preferences across devices when logged in
- **FR-037**: System MUST support account creation via email and social login providers (Google, Apple)
- **FR-038**: System MUST allow full app functionality without account creation, using local-only storage
- **FR-039**: System MUST persist user preferences and settings locally when not logged in
- **FR-040**: System MUST automatically refresh current program data to reflect schedule changes in real-time
- **FR-041**: System MUST cache EPG data for offline viewing with clear indication when data is stale
- **FR-042**: System MUST handle missing or incomplete program data gracefully with appropriate placeholders
- **FR-043**: System MUST provide an intuitive, beginner-friendly interface despite advanced features availability
- **FR-044**: System MUST delete anonymous user data (favorite channels and programs only) after 180 days of inactivity
- **FR-045**: System MUST delete logged-in user data after 2 years of inactivity, sending warning emails 30 days and 7 days before deletion
- **FR-046**: System MUST NOT store viewing history or recommendation data for anonymous users beyond what's necessary for current session recommendations
- **FR-047**: System MUST require passwords to be at least 8 characters with mixed case letters and numbers
- **FR-048**: System MUST hash all passwords using bcrypt or Argon2 algorithm before storage
- **FR-049**: System MUST implement OAuth2 protocol for social login integration (Google, Apple)
- **FR-050**: System MUST NOT implement two-factor authentication (2FA) as the app does not handle sensitive personal data
- **FR-051**: System MUST fallback to cached EPG data (up to 24 hours old) when primary data source is unavailable
- **FR-052**: System MUST display a clear "Data may be outdated" indicator when showing cached EPG data older than the last successful update
- **FR-053**: System MUST cache EPG data for up to 24 hours to enable offline functionality
- **FR-054**: System MUST merge non-conflicting changes during sync (e.g., favorite programs added on different devices are combined into a union set)
- **FR-055**: System MUST resolve conflicting setting changes using last-write-wins strategy based on modification timestamps
- **FR-056**: System MUST timestamp all user settings and data modifications to enable conflict resolution during synchronization
- **FR-057**: System MUST schedule program reminder notifications locally on the device (using exact alarms on Android, local notifications on iOS) as the primary notification method
- **FR-058**: System MUST use push notifications as a secondary backup method when local notification scheduling fails
- **FR-059**: System MUST send push notifications to trigger EPG data refresh when program schedules change on the server
- **FR-060**: System MUST ensure notifications work offline by relying primarily on local device scheduling

### Key Entities

- **Channel**: Represents a TV channel with name, logo, channel number (optional), category, broadcast technology (terrestrial, satellite, cable, streaming), and operator association
- **Program**: Represents a TV program with title, description, genre, duration, start time, end time, age rating, and type (movie, series, sports, documentary, etc.)
- **Episode**: For series, extends Program with season number, episode number, and series title
- **Cast Member**: Person involved in a program with name, role (actor, director, writer, etc.), photo, and relationship to programs
- **User**: Represents app user with optional account credentials, device identifiers for local storage
- **Favorite Channel List**: Named collection of channels with ordering and active/inactive status
- **Favorite Program**: User's favorited program with notification settings (reminder times) and tracking of upcoming airings
- **User Preferences**: User's theme choice, navigation customization, favorite genres, favorite sports, favorite creators
- **Notification**: Scheduled reminder for a program with trigger time, program reference, and delivery status
- **Rating**: External rating from ČSFD or IMDB with score and link to full review

### Non-Functional Requirements

- **NFR-001**: App MUST maintain responsive performance (60fps) when scrolling through EPG grid or program lists
- **NFR-002**: Program data MUST update automatically in the background without user intervention
- **NFR-003**: App MUST support offline mode with cached data, displaying staleness indicators
- **NFR-004**: App MUST follow Material 3 design guidelines for consistent, modern UI
- **NFR-005**: App MUST be accessible to users with disabilities, supporting screen readers and sufficient color contrast (WCAG AA minimum)
- **NFR-006**: App MUST support multiple languages (minimally Czech and English)
- **NFR-007**: App MUST adapt layouts for different screen sizes (phones, tablets, foldables, desktop)
- **NFR-008**: App MUST work on Android (API 24+), iOS (15+), web browsers, and desktop (Windows, macOS, Linux)
- **NFR-009**: App MUST comply with GDPR privacy requirements, implementing minimal data collection for anonymous users (favorite channels and programs only)
- **NFR-010**: App MUST send email notifications to logged-in users 30 days and 7 days before account deletion due to 2-year inactivity
- **NFR-011**: App MUST implement secure password storage using industry-standard hashing algorithms (bcrypt or Argon2)
- **NFR-012**: App MUST use HTTPS for all network communications to protect user credentials and data in transit
- **NFR-013**: App MUST ensure notification reliability by using local device scheduling as primary method, ensuring notifications work even when device is offline

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can find what's currently on their favorite channels within 3 seconds of app launch
- **SC-002**: Users can complete favorite channel setup in under 2 minutes during first launch
- **SC-003**: 90% of users successfully navigate to program details from any view on their first attempt
- **SC-004**: EPG grid view renders and allows smooth scrolling for up to 100 channels simultaneously
- **SC-005**: Program detail pages load within 1 second, including external ratings
- **SC-006**: Notifications deliver accurately within 30 seconds of scheduled time
- **SC-007**: Users can switch between themes and see changes reflected immediately
- **SC-008**: Account login and settings sync complete within 5 seconds on a standard broadband connection
- **SC-009**: App launches and displays cached data within 2 seconds even without internet connection
- **SC-010**: 80% of users rate the app interface as "easy to use" or "very easy to use" regardless of technical skill level
- **SC-011**: Widget updates reflect current program information within 1 minute of program change
- **SC-012**: Search results for programs, actors, or directors return within 1 second for queries
- **SC-013**: Recommendations achieve at least 40% relevance rate (users mark recommended content as interesting)

## Assumptions

1. EPG data will be sourced from a reliable third-party provider or public API with coverage of all major Czech and Slovak channels
2. Users have internet connectivity for initial setup and periodic updates, but app should function offline with cached data
3. External rating services (ČSFD, IMDB) have public APIs or scraping is permissible for displaying ratings
4. Notification delivery relies on platform notification services (FCM for Android, APNs for iOS)
5. Most users will have 10-30 favorite channels; the system should optimize for this range while supporting up to 100+
6. Account synchronization will use a backend service (implementation details not specified here)
7. Widget functionality is platform-dependent and may have different capabilities on Android vs iOS vs web vs desktop
8. Program images, cast photos, and promotional materials are available from EPG provider or can be sourced from public databases
9. Users primarily use the app in their native language (Czech/Slovak), but international users should have English support
10. The app will comply with GDPR and local privacy regulations regarding user data and tracking

## Out of Scope

The following features are explicitly **not** included in this specification:

1. Streaming or playback of TV content within the app
2. Recording or DVR functionality
3. Live TV streaming integration
4. Social features (sharing, comments, reviews)
5. Integration with smart TV remote controls or TV hardware
6. Parental controls or PIN protection for content
7. Subscription or payment processing for premium features
8. User-generated content (user reviews, ratings, watch lists beyond favorites)
9. Integration with specific TV service provider apps or authentication systems
10. Offline download of program information beyond automatic caching
11. Calendar integration to add programs to device calendar
12. Cross-platform voice control or virtual assistant integration
13. Picture-in-picture or mini-player modes
14. Chromecast or AirPlay support for casting
15. Custom alerts beyond the two standard notification times (e.g., SMS, email notifications)

## Dependencies

1. **EPG Data Provider**: Requires partnership or API access to reliable TV schedule data source
2. **External Rating Services**: ČSFD and IMDB APIs or data access
3. **Platform SDKs**: Android SDK, iOS SDK, Compose Multiplatform, Desktop JVM
4. **Local Notification Services**: Android AlarmManager (exact alarms), iOS Local Notifications framework
5. **Push Notification Services**: Firebase Cloud Messaging (Android), Apple Push Notification Service (iOS) - for backup notifications and schedule change alerts
6. **Backend Services**: For account management, settings synchronization, and push notification coordination
7. **Image CDN**: For serving channel logos, program images, cast photos
8. **Analytics Service**: For tracking usage patterns to improve recommendations (if implementing personalization)
