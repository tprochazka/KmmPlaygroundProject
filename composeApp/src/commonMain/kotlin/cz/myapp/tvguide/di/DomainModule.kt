package cz.myapp.tvguide.di

/**
 * Domain layer dependency container.
 * 
 * Provides use cases that encapsulate business logic.
 * 
 * Use cases will be added as we implement each user story:
 * - Phase 4 (US1): GetCurrentProgramsUseCase, GetFavoriteChannelsUseCase
 * - Phase 5 (US2): GetAllChannelsUseCase, UpdateFavoriteChannelsUseCase, etc.
 * - Phase 6 (US3): GetEpgDataUseCase
 * - Phase 7 (US4): GetChronologicalProgramsUseCase, SearchProgramsUseCase
 * - Phase 7 (US10): GetUserPreferencesUseCase, UpdateUserPreferencesUseCase
 * - Phase 8 (US5): GetProgramDetailsUseCase, GetSimilarProgramsUseCase
 * 
 * Phase 1: Simple singleton pattern
 * Phase 2+: Migrate to Metro DI when dependency injection becomes more complex
 */
object DomainModule {
    
    // Use cases will be added here in subsequent phases
    // For now, this module is a placeholder
}
