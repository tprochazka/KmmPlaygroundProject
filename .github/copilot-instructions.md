# GitHub Copilot Instructions

This project uses SpecKit methodology with custom prompts.

## Available Custom Prompts

Use the `/` command in chat to access these prompts:

- **speckit.constitution** - Create or update the project constitution from interactive or provided principle inputs
- **speckit.analyze** - Analyze project structure and dependencies
- **speckit.checklist** - Generate and validate project checklists
- **speckit.clarify** - Clarify requirements and specifications
- **speckit.implement** - Implement features based on specifications
- **speckit.plan** - Create project plans
- **speckit.specify** - Create detailed specifications
- **speckit.tasks** - Generate and manage task lists

## How to Use

1. Open GitHub Copilot Chat
2. Type `/` to see available commands
3. Select a `speckit.*` prompt
4. Follow the interactive prompts or provide arguments

## Project Context

All prompts work with templates in `.specify/` directory and follow the project constitution at `.specify/memory/constitution.md`.

### Current Feature: TV Program Guide Application (001-tv-guide-app)

**Technology Stack**:

- **Language**: Kotlin 2.2.21
- **Framework**: Compose Multiplatform 1.9.1
- **Platforms**: Android (API 24+), iOS (15+), Web (Wasm/JS), Desktop (JVM)
- **Architecture**: Clean Architecture (Presentation → Domain → Data)
- **Dependency Injection**: Metro DI
- **Navigation**: Voyager (planned migration to Navigation 3)
- **Database**: Room (cross-platform)
- **Image Loading**: Coil 3
- **HTTP Client**: Ktor 3.x (future)
- **Logging**: Kermit
- **Design System**: Material 3 with adaptive layouts

**Project Structure**:

```
composeApp/src/
├── commonMain/          # Shared code
│   ├── domain/         # Use cases, entities
│   ├── data/           # Repositories, data sources
│   └── presentation/   # ViewModels, screens
├── androidMain/        # Android-specific
├── iosMain/            # iOS-specific
├── jvmMain/            # Desktop-specific
├── wasmJsMain/         # Web-specific
└── jsMain/             # JS fallback
```

**Phase 1 Prototype** (Current):

- Mock data at repository layer (Kotlin DSL)
- No real API integration
- 6 iterations: Scaffold → Home → Favorites → EPG → List/Detail → Polish
- Focus: UX validation, adaptive layouts, cross-platform compatibility

**Key Documents**:

- Constitution: `.specify/memory/constitution.md`
- Specification: `specs/001-tv-guide-app/spec.md`
- Implementation Plan: `specs/001-tv-guide-app/plan.md`
- Data Model: `specs/001-tv-guide-app/data-model.md`
- API Contract: `specs/001-tv-guide-app/contracts/epg-api.yaml`
- Quick Start: `specs/001-tv-guide-app/quickstart.md`

**Development Guidelines**:

1. Follow Clean Architecture: Presentation depends on Domain, Domain independent of frameworks
2. Use Metro DI for all dependency injection
3. Keep UI code in commonMain using Compose Multiplatform
4. Platform-specific code only for native APIs (notifications, etc.)
5. Material 3 design with adaptive layouts (WindowSizeClass)
6. Test-first approach deferred to Phase 2 (prototype phase)
7. Mock repositories with simulated delays: `delay(300..800)`

