# AI Agent Instructions

This project uses **SpecKit methodology** with custom prompts for structured development.

> **Note**: This file is read by GitHub Copilot, Claude, Cursor, and other AI coding assistants.

## Available Custom Prompts

Use the `/` command in chat (or equivalent) to access these prompts:

- **speckit.constitution** - Create or update the project constitution from interactive or provided principle inputs
- **speckit.analyze** - Analyze project structure and dependencies
- **speckit.checklist** - Generate and validate project checklists
- **speckit.clarify** - Clarify requirements and specifications
- **speckit.implement** - Implement features based on specifications
- **speckit.plan** - Create project plans
- **speckit.specify** - Create detailed specifications
- **speckit.tasks** - Generate and manage task lists

## How to Use

### GitHub Copilot

1. Open GitHub Copilot Chat
2. Type `/` to see available commands
3. Select a `speckit.*` prompt
4. Follow the interactive prompts or provide arguments

### Claude Code / Cursor / Other Agents

1. Reference this file in your instructions
2. Mention the specific `speckit.*` prompt you want to use
3. Provide context from the relevant specification files

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
- Task List: `specs/001-tv-guide-app/tasks.md`

**Development Guidelines**:

1. Follow Clean Architecture: Presentation depends on Domain, Domain independent of frameworks
2. Use Metro DI for all dependency injection
3. Keep UI code in commonMain using Compose Multiplatform
4. Platform-specific code only for native APIs (notifications, etc.)
5. Material 3 design with adaptive layouts (WindowSizeClass)
6. Test-first approach deferred to Phase 2 (prototype phase)
7. Mock repositories with simulated delays: `delay(300..800)`

## File Paths

**SpecKit Templates**: `.specify/templates/`  
**Feature Specifications**: `specs/001-tv-guide-app/`  
**Source Code**: `composeApp/src/commonMain/kotlin/cz/myapp/tvguide/`

## SpecKit Workflow

1. **Constitution** → Define project principles and governance
2. **Specify** → Create detailed feature specifications with user stories
3. **Clarify** → Resolve ambiguities through Q&A
4. **Plan** → Create implementation plan with iterations
5. **Tasks** → Generate executable task breakdown
6. **Implement** → Execute tasks following the plan

## Current Phase

**Phase 1 Prototype** - Task execution phase

- 161 tasks organized into 8 phases
- Focus: US1-US5 and US10 implementation
- MVP: Phases 1-4 (Setup + Foundational + Navigation + Home Screen)
- Full prototype: All 8 phases

See `specs/001-tv-guide-app/tasks.md` for complete task list.
