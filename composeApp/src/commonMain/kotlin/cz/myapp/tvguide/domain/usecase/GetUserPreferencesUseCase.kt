package cz.myapp.tvguide.domain.usecase

import cz.myapp.tvguide.domain.model.UserPreferences
import cz.myapp.tvguide.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving user preferences.
 * 
 * Returns a Flow of UserPreferences that updates whenever preferences change.
 */
class GetUserPreferencesUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(userId: String = "default_user"): Flow<UserPreferences> {
        return userPreferencesRepository.getUserPreferences(userId)
    }
}
