package cz.myapp.tvguide.domain.usecase

import cz.myapp.tvguide.domain.model.UserPreferences
import cz.myapp.tvguide.domain.repository.UserPreferencesRepository

/**
 * Use case for updating user preferences.
 * 
 * Persists changes to user preferences (theme, time format, compact mode, etc.).
 */
class UpdateUserPreferencesUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(preferences: UserPreferences) {
        userPreferencesRepository.updateUserPreferences(preferences)
    }
}
