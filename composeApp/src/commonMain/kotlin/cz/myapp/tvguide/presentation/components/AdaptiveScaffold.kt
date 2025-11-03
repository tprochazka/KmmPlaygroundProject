package cz.myapp.tvguide.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab

/**
 * Adaptive scaffold that provides responsive navigation based on screen size.
 * 
 * Uses Material 3 Adaptive API to automatically switch between:
 * - Bottom navigation bar (Compact screens - phones)
 * - Navigation rail (Medium/Expanded screens - tablets/desktop)
 * 
 * Integrates with Voyager TabNavigator for tab-based navigation.
 */
@Composable
fun AdaptiveScaffold(
    tabs: List<Tab>,
    modifier: Modifier = Modifier,
) {
    val tabNavigator = LocalTabNavigator.current
    
    // Use modern Material 3 Adaptive API
    val adaptiveInfo = currentWindowAdaptiveInfo()
    
    // Determine navigation suite type based on window size class
    val navigationSuiteType = when (adaptiveInfo.windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> NavigationSuiteType.NavigationBar
        WindowWidthSizeClass.MEDIUM -> NavigationSuiteType.NavigationRail
        WindowWidthSizeClass.EXPANDED -> NavigationSuiteType.NavigationRail
        else -> NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
    }
    
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            tabs.forEach { tab ->
                // Safe check: only compare if current is actually a Tab
                val isSelected = (tabNavigator.current as? Tab) == tab
                item(
                    selected = isSelected,
                    onClick = { tabNavigator.current = tab },
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
        },
        layoutType = navigationSuiteType,
        modifier = modifier
    ) {
        // Display current tab content
        CurrentTab()
    }
}
