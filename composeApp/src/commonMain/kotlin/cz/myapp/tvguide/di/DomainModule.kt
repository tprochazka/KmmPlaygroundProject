package cz.myapp.tvguide.di

import cz.myapp.tvguide.domain.usecase.CreateChannelListUseCase
import cz.myapp.tvguide.domain.usecase.GetAllChannelsUseCase
import cz.myapp.tvguide.domain.usecase.GetCurrentProgramsUseCase
import cz.myapp.tvguide.domain.usecase.GetEpgDataUseCase
import cz.myapp.tvguide.domain.usecase.GetFavoriteChannelsUseCase
import cz.myapp.tvguide.domain.usecase.UpdateFavoriteChannelsUseCase

/**
 * Domain layer dependency container.
 * 
 * Provides use cases that encapsulate business logic.
 * 
 * Use cases implemented:
 * - Phase 4 (US1): GetCurrentProgramsUseCase, GetFavoriteChannelsUseCase ✅
 * - Phase 5 (US2): GetAllChannelsUseCase, UpdateFavoriteChannelsUseCase, CreateChannelListUseCase ✅
 * - Phase 6 (US3): GetEpgDataUseCase ✅
 * 
 * Upcoming use cases:
 * - Phase 7 (US4): GetChronologicalProgramsUseCase, SearchProgramsUseCase
 * - Phase 7 (US10): GetUserPreferencesUseCase, UpdateUserPreferencesUseCase
 * - Phase 8 (US5): GetProgramDetailsUseCase, GetSimilarProgramsUseCase
 * 
 * Phase 1: Simple singleton pattern
 * Phase 2+: Migrate to Metro DI when dependency injection becomes more complex
 */
object DomainModule {
    
    /**
     * Use case to get current programs for channels.
     * US1: Current TV View
     */
    val getCurrentProgramsUseCase: GetCurrentProgramsUseCase by lazy {
        GetCurrentProgramsUseCase(
            channelRepository = DataModule.channelRepository,
            programRepository = DataModule.programRepository
        )
    }
    
    /**
     * Use case to get user's favorite channels.
     * US2: Favorites Management
     */
    val getFavoriteChannelsUseCase: GetFavoriteChannelsUseCase by lazy {
        GetFavoriteChannelsUseCase(
            channelRepository = DataModule.channelRepository,
            userPreferencesRepository = DataModule.userPreferencesRepository
        )
    }
    
    /**
     * Use case to get all available channels.
     * US2: Favorites Management
     */
    val getAllChannelsUseCase: GetAllChannelsUseCase by lazy {
        GetAllChannelsUseCase(
            channelRepository = DataModule.channelRepository
        )
    }
    
    /**
     * Use case to update favorite channels list.
     * US2: Favorites Management
     */
    val updateFavoriteChannelsUseCase: UpdateFavoriteChannelsUseCase by lazy {
        UpdateFavoriteChannelsUseCase(
            userPreferencesRepository = DataModule.userPreferencesRepository
        )
    }
    
    /**
     * Use case to create custom channel lists.
     * US2: Favorites Management
     */
    val createChannelListUseCase: CreateChannelListUseCase by lazy {
        CreateChannelListUseCase(
            userPreferencesRepository = DataModule.userPreferencesRepository
        )
    }
    
    /**
     * Use case to get EPG (Electronic Program Guide) data.
     * US3: EPG Grid View
     */
    val getEpgDataUseCase: GetEpgDataUseCase by lazy {
        GetEpgDataUseCase(
            channelRepository = DataModule.channelRepository,
            programRepository = DataModule.programRepository,
            userPreferencesRepository = DataModule.userPreferencesRepository
        )
    }
}
