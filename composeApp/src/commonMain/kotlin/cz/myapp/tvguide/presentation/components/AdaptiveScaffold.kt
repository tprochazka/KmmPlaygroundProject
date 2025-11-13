package cz.myapp.tvguide.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab

/**
 * CompositionLocal to track if navigation should be shown.
 * Updated by nested navigators when they push/pop screens.
 */
val LocalNavigationVisible = compositionLocalOf { mutableStateOf(true) }

/**
 * Adaptive scaffold that provides responsive navigation based on screen size.
 *
 * Uses Material 3 Adaptive API to automatically switch between:
 * - Bottom navigation bar (Compact screens - phones)
 * - Navigation rail (Medium/Expanded screens - tablets/desktop)
 *
 * Integrates with Voyager TabNavigator for tab-based navigation.
 *
 * Navigation visibility is controlled via LocalNavigationVisible, which is updated
 * by tab navigators when they push/pop detail screens.
 */
@Composable
fun AdaptiveScaffold(
    tabs: List<Tab>,
    modifier: Modifier = Modifier,
) {
    val tabNavigator = LocalTabNavigator.current

    // Shared state for navigation visibility (updated by tab navigators)
    val navigationVisible = remember { mutableStateOf(true) }

    // Use modern Material 3 Adaptive API
    val adaptiveInfo = currentWindowAdaptiveInfo()

    // Determine navigation suite type based on window size class
    // If navigation is hidden, use None to avoid reserving space
    val navigationSuiteType = if (!navigationVisible.value) {
        NavigationSuiteType.None
    } else {
        when (adaptiveInfo.windowSizeClass.windowWidthSizeClass) {
            WindowWidthSizeClass.COMPACT -> NavigationSuiteType.NavigationBar
            WindowWidthSizeClass.MEDIUM -> NavigationSuiteType.NavigationRail
            WindowWidthSizeClass.EXPANDED -> NavigationSuiteType.NavigationRail
            else -> NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
        }
    }

    CompositionLocalProvider(LocalNavigationVisible provides navigationVisible) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                if (navigationVisible.value) {
                    tabs.forEach { tab ->
                        val isSelected = tabNavigator.current == tab
                        item(
                            selected = isSelected,
                            onClick = {
                                tabNavigator.current = tab
                            },
                            icon = {
                                tab.options.icon?.let { painter ->
                                    Icon(
                                        painter = painter,
                                        contentDescription = tab.options.title
                                    )
                                }
                            },
                            label = {
                                Text(text = tab.options.title)
                            }
                        )
                    }
                }
            },
            layoutType = navigationSuiteType,
            modifier = modifier
        ) {
            // Display selected tab content with slide animation
            // Each tab content is wrapped in key() to maintain separate state
            AnimatedContent(
                targetState = tabNavigator.current,
                transitionSpec = {
                    val targetIndex = tabs.indexOf(targetState)
                    val initialIndex = tabs.indexOf(initialState)

                    // Determine slide direction based on tab index
                    val slideDirection = if (targetIndex > initialIndex) {
                        // Sliding to the right (next tab)
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(300)
                        ) togetherWith slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(300)
                        )
                    } else {
                        // Sliding to the left (previous tab)
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(300)
                        ) togetherWith slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(300)
                        )
                    }
                    slideDirection
                },
                label = "TabTransition"
            ) { tab ->
                // Use key() to ensure each tab has unique composition identity
                key(tab.key) {
                    tab.Content()
                }
            }
        }
    }
}

