<!--
SYNC IMPACT REPORT
==================
Version Change: Initial → 1.0.0
Modified Principles: N/A (initial constitution)
Added Sections:
  - Core Principles (7 principles for KMP development)
  - Technology Stack Requirements
  - Architecture Standards
  - Development Workflow
  - Governance
Removed Sections: N/A
Templates Status:
  ✅ plan-template.md - Reviewed, compatible with constitution principles
  ✅ spec-template.md - Reviewed, compatible with constitution principles
  ✅ tasks-template.md - Reviewed, compatible with constitution principles
Follow-up TODOs:
  - RATIFICATION_DATE is set to today (2025-11-02) as initial adoption
-->

# KmmPlaygroundProject Constitution

## Core Principles

### I. Multiplatform-First Architecture

All features MUST be designed for maximum code sharing across Android, iOS, web, and desktop
platforms. Platform-specific code is permitted only when:

- Native platform APIs are required for functionality not available in common code
- Performance optimization requires platform-specific implementation
- UX guidelines demand platform-specific behavior (e.g., navigation patterns)

**Rationale**: Kotlin Multiplatform enables up to 90% code sharing, reducing development time,
bugs, and maintenance burden. This principle ensures we maximize this benefit while respecting
platform conventions where necessary.

### II. Compose-Native UI (NON-NEGOTIABLE)

UI implementation MUST use Compose Multiplatform exclusively. No hybrid approaches (XML layouts,
SwiftUI, HTML) are permitted except for:

- iOS-specific SwiftUI entry points (iosApp wrapper only)
- Platform-specific custom views when Compose API is demonstrably insufficient

All UI components MUST be:

- Adaptive to screen sizes (phone, tablet, desktop, foldables)
- Responsive to orientation changes
- Compliant with Material 3 Design guidelines

**Rationale**: Compose Multiplatform provides declarative, type-safe UI across all platforms with
consistent behavior. Material 3 ensures modern, accessible design. Adaptive layout maximizes
usability across device types.

### III. Test-First Development (NON-NEGOTIABLE)

TDD cycle MUST be followed: Write test → Verify test fails → Implement → Verify test passes →
Refactor. Tests MUST be written before implementation code.

All code MUST have:

- Unit tests for business logic (in commonTest when possible)
- Platform-specific tests only for platform-specific code
- Integration tests for cross-module interactions
- UI tests for critical user journeys (using Compose UI testing framework)

Minimum coverage target: 80% for common code, 60% for platform-specific code.

**Rationale**: TDD ensures testability by design, catches regressions early, and serves as
living documentation. KMP's expect/actual mechanism allows most tests to live in commonTest,
reducing test duplication.

### IV. Clean Architecture with Clear Boundaries

Projects MUST follow layered architecture:

- **Presentation Layer**: Compose UI + ViewModels (commonMain/platform-specific)
- **Domain Layer**: Business logic, use cases, domain models (commonMain)
- **Data Layer**: Repositories, data sources, networking (commonMain + platform implementations)

Dependencies MUST flow inward only (Presentation → Domain → Data). Dependency inversion MUST be
used for cross-layer communication (interfaces in domain, implementations in data/presentation).

**Rationale**: Clean architecture ensures testability, maintainability, and platform
independence. Domain logic in commonMain maximizes code sharing and business rule consistency.

### V. Metro DI for Dependency Injection

Dependency injection MUST use Metro DI framework. Manual dependency management is prohibited
except in:

- Compose @Composable functions (via remember/rememberSaveable)
- Entry points before DI container initialization

All modules, repositories, ViewModels, and use cases MUST be registered in DI container with
appropriate scopes (singleton, factory, scoped).

**Rationale**: Metro DI is KMP-native, lightweight, and supports all platforms. Centralized DI
ensures testability (easy mocking), loose coupling, and prevents tight binding to concrete
implementations.

### VI. User Experience Excellence

All UI implementations MUST prioritize:

- **Performance**: 60fps animations, <100ms interaction response time
- **Accessibility**: Screen reader support, sufficient color contrast (WCAG AA minimum)
- **Feedback**: Loading states, error messages, success confirmations
- **Consistency**: Uniform spacing, typography, color usage per Material 3 tokens
- **Progressive Enhancement**: Graceful degradation on older devices/browsers

User-facing errors MUST be human-readable, actionable, and never expose technical details.

**Rationale**: Perfect UX differentiates products. Material 3 provides solid foundations,
but teams must validate with real users. Accessibility is non-negotiable for inclusive design.

### VII. Code Quality and Clarity

All code MUST be:

- **Readable**: Self-documenting names, clear control flow, KDoc for public APIs
- **Idiomatic**: Follow Kotlin coding conventions, leverage language features appropriately
- **Simple**: Prefer clear code over clever code (YAGNI, KISS principles)
- **Reviewed**: No code merged without peer review and CI checks passing

Complexity MUST be justified. Over-engineering is a defect. Premature optimization is prohibited.

Code formatting MUST follow Kotlin official style guide (enforced via ktlint).

**Rationale**: Quality and clarity compound over time. Readable code reduces onboarding time,
bug rates, and maintenance costs. Consistency via tooling (ktlint) eliminates bikeshedding.

## Technology Stack Requirements

**Mandatory Technologies**:

- Kotlin 2.2+ with Compose Multiplatform 1.9+
- Compose UI for all platforms (including Compose for iOS)
- Material 3 design system (androidx.compose.material3)
- Metro DI for dependency injection
- Kotlin Coroutines for async operations
- Kotlin Serialization for JSON/data serialization

**Target Platforms**:

- Android: API 24+ (Android 7.0+), target SDK 36
- iOS: iOS 15+ (via Compose for iOS, Xcode project wrapper)
- Web: Modern browsers via Wasm (primary), JS fallback for legacy support
- Desktop: JVM-based (Windows, macOS, Linux)

**Testing Stack**:

- kotlin-test for common tests
- JUnit for JVM/Android tests
- XCTest integration for iOS tests (when platform-specific)
- Compose UI Testing for UI tests

**Prohibited**:

- Legacy Android View system (XML layouts)
- Mixing UI frameworks (SwiftUI UI logic, React components, etc.)
- Platform-specific dependency injection frameworks (Hilt, Koin, except as wrappers if needed)

## Architecture Standards

**Module Organization**:

- Prefer feature modules over layer modules for scalability
- Each feature module MUST have its own DI module
- Shared code in `commonMain`, platform specifics in `androidMain`, `iosMain`, etc.

**State Management**:

- Use ViewModel + StateFlow/State for UI state
- Single source of truth per screen
- Unidirectional data flow (UDF pattern)

**Navigation**:

- Type-safe navigation using Compose Navigation (or similar KMP-compatible solution)
- Deep linking support for all platforms where applicable

**Error Handling**:

- Use Result/Either types for operations that can fail
- Structured error types (sealed classes/interfaces)
- Centralized error logging and reporting

**Performance Requirements**:

- App startup time: <2 seconds on mid-range devices
- Screen navigation: <16ms frame time (60fps)
- Network requests: Proper caching, offline support where applicable

## Development Workflow

**Version Control**:

- Git flow or trunk-based development (team decision documented in README)
- Feature branches: `###-feature-name` format
- Commit messages: Conventional Commits format (feat:, fix:, docs:, etc.)

**Code Review Process**:

1. All code MUST be reviewed by at least one peer before merge
2. CI checks (build, tests, linting) MUST pass
3. Constitution compliance MUST be verified
4. Performance impact MUST be assessed for critical paths

**Quality Gates**:

- No compiler warnings allowed in production code
- Test coverage MUST meet minimum thresholds (80% common, 60% platform)
- No critical/high security vulnerabilities (dependency scanning)
- Performance budgets MUST not be exceeded

**Documentation Requirements**:

- Public APIs MUST have KDoc
- Complex algorithms MUST have explanatory comments
- Architecture decisions MUST be documented in ADR format (in `.specify/adr/`)
- README MUST be kept current with setup instructions

## Governance

This constitution supersedes all other development practices. When conflicts arise, constitution
principles take precedence.

**Amendment Process**:

1. Proposed amendments MUST be documented with rationale
2. Team review and consensus required
3. Version bump following semantic versioning (see below)
4. Migration plan required for breaking changes
5. All affected templates and documentation MUST be updated

**Version Semantics**:

- MAJOR: Backward-incompatible governance changes, principle removals/redefinitions
- MINOR: New principles added, sections expanded with new requirements
- PATCH: Clarifications, wording improvements, typo fixes

**Compliance Verification**:

- All pull requests MUST include constitution compliance self-check
- Quarterly compliance audits for existing codebase
- Violations MUST be addressed or formally justified and documented

**Complexity Justification**:
Any deviation from principles (e.g., using platform-specific UI, skipping tests for specific
code) MUST be documented in the relevant plan.md under "Complexity Tracking" section with:

- Specific principle violated
- Technical justification
- Alternative approaches considered
- Mitigation plan

**Version**: 1.0.0 | **Ratified**: 2025-11-02 | **Last Amended**: 2025-11-02

