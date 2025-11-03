package cz.myapp.tvguide.presentation.screens.settings

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cz.myapp.tvguide.domain.model.UserPreferences
import cz.myapp.tvguide.domain.usecase.GetUserPreferencesUseCase
import cz.myapp.tvguide.domain.usecase.UpdateUserPreferencesUseCase
import cz.myapp.tvguide.presentation.theme.ThemeMode
import cz.myapp.tvguide.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ScreenModel for Settings screen.
 * 
 * Manages user preferences including:
 * - Theme mode (Light/Dark/System)
 * - Time format (12h/24h)
 * - Compact mode toggle
 * - Navigation item customization
 * 
 * US10: Theme & Navigation Customization
 */
class SettingsScreenModel(
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updateUserPreferencesUseCase: UpdateUserPreferencesUseCase,
    private val userId: String = "default_user"
) : ScreenModel {
    
    private val tag = "SettingsScreenModel"
    
    // User preferences from repository
    val preferences: StateFlow<UserPreferences> = getUserPreferencesUseCase(userId)
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences(userId = userId)
        )
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        AppLogger.i(tag) { "SettingsScreenModel initialized for userId=$userId" }
    }
    
    /**
     * Update theme mode preference.
     */
    fun updateThemeMode(themeMode: ThemeMode) {
        AppLogger.d(tag) { "updateThemeMode: $themeMode" }
        screenModelScope.launch {
            _isLoading.value = true
            try {
                val updated = preferences.value.copy(themeMode = themeMode)
                updateUserPreferencesUseCase(updated)
                AppLogger.i(tag) { "Theme mode updated to $themeMode" }
            } catch (e: Exception) {
                AppLogger.e(tag, e) { "Failed to update theme mode" }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Toggle time format between 12h and 24h.
     */
    fun toggleTimeFormat() {
        AppLogger.d(tag) { "toggleTimeFormat: current=${preferences.value.use24HourFormat}" }
        screenModelScope.launch {
            _isLoading.value = true
            try {
                val updated = preferences.value.copy(
                    use24HourFormat = !preferences.value.use24HourFormat
                )
                updateUserPreferencesUseCase(updated)
                AppLogger.i(tag) { "Time format toggled to ${updated.use24HourFormat}" }
            } catch (e: Exception) {
                AppLogger.e(tag, e) { "Failed to toggle time format" }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Toggle compact mode.
     */
    fun toggleCompactMode() {
        AppLogger.d(tag) { "toggleCompactMode: current=${preferences.value.compactMode}" }
        screenModelScope.launch {
            _isLoading.value = true
            try {
                val updated = preferences.value.copy(
                    compactMode = !preferences.value.compactMode
                )
                updateUserPreferencesUseCase(updated)
                AppLogger.i(tag) { "Compact mode toggled to ${updated.compactMode}" }
            } catch (e: Exception) {
                AppLogger.e(tag, e) { "Failed to toggle compact mode" }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Toggle notifications.
     */
    fun toggleNotifications() {
        AppLogger.d(tag) { "toggleNotifications: current=${preferences.value.notificationsEnabled}" }
        screenModelScope.launch {
            _isLoading.value = true
            try {
                val updated = preferences.value.copy(
                    notificationsEnabled = !preferences.value.notificationsEnabled
                )
                updateUserPreferencesUseCase(updated)
                AppLogger.i(tag) { "Notifications toggled to ${updated.notificationsEnabled}" }
            } catch (e: Exception) {
                AppLogger.e(tag, e) { "Failed to toggle notifications" }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
