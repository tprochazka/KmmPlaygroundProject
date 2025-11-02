package cz.myapp.tvguide.di

import cz.myapp.tvguide.data.repository.LocalUserPreferencesRepository
import cz.myapp.tvguide.data.repository.MockChannelRepository
import cz.myapp.tvguide.data.repository.MockProgramRepository
import cz.myapp.tvguide.domain.repository.ChannelRepository
import cz.myapp.tvguide.domain.repository.ProgramRepository
import cz.myapp.tvguide.domain.repository.UserPreferencesRepository

/**
 * Data layer dependency container.
 * 
 * Provides repository implementations to the rest of the app.
 * 
 * Phase 1: Simple singleton pattern with lazy initialization
 * Phase 2+: Migrate to Metro DI when more complex dependency graphs are needed
 */
object DataModule {
    
    /**
     * ChannelRepository singleton.
     * 
     * Phase 1: MockChannelRepository with static mock data
     * Phase 2+: RoomChannelRepository with database + API sync
     */
    val channelRepository: ChannelRepository by lazy {
        MockChannelRepository()
    }
    
    /**
     * ProgramRepository singleton.
     * 
     * Phase 1: MockProgramRepository with generated program data
     * Phase 2+: RoomProgramRepository with database + API sync
     */
    val programRepository: ProgramRepository by lazy {
        MockProgramRepository()
    }
    
    /**
     * UserPreferencesRepository singleton.
     * 
     * Phase 1: LocalUserPreferencesRepository with in-memory storage
     * Phase 2+: RoomUserPreferencesRepository with persistent storage
     */
    val userPreferencesRepository: UserPreferencesRepository by lazy {
        LocalUserPreferencesRepository()
    }
}
