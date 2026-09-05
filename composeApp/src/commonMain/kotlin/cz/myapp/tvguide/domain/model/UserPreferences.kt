package cz.myapp.tvguide.domain.model

import cz.myapp.tvguide.presentation.theme.ThemeMode

/**
 * User preferences for the TV Guide application.
 * 
 * @property userId User identifier (for multi-user support in future)
 * @property themeMode Theme mode preference (SYSTEM, LIGHT, DARK)
 * @property use24HourFormat Whether to use 24-hour time format
 * @property compactMode Whether to use compact UI mode
 * @property defaultChannelListId ID of the default favorite channel list
 * @property notificationsEnabled Whether notifications are globally enabled
 * @property navigationItems Ordered list of visible navigation items
 */
data class UserPreferences(
    val userId: String = "default",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val use24HourFormat: Boolean = true,
    val compactMode: Boolean = false,
    val defaultChannelListId: String? = null,
    val notificationsEnabled: Boolean = true,
    val navigationItems: List<NavigationItem> = NavigationItem.defaultItems()
) {
    init {
        require(userId.isNotBlank()) { "User ID must not be blank" }
    }
}

/**
 * Navigation items that can be shown/hidden and reordered.
 */
enum class NavigationItem(val displayName: String, val icon: String) {
    HOME("Nyní na TV", "home"),
    FAVORITES("Oblíbené", "favorite"),
    EPG("EPG", "grid_on"),
    LIST("Seznam", "list"),
    SETTINGS("Nastavení", "settings");
    
    companion object {
        /**
         * Default navigation items in default order.
         */
        fun defaultItems(): List<NavigationItem> = listOf(
            HOME,
            FAVORITES,
            EPG,
            LIST,
            SETTINGS
        )
    }
}
