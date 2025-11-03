package cz.myapp.tvguide.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass
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
    
    // Remember the selected tab to avoid accessing tabNavigator.current
    // which crashes when DetailScreen (non-Tab) is pushed onto navigator
    var selectedTab by remember { mutableStateOf<Tab>(tabs.first()) }
    
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
                val isSelected = selectedTab == tab
                item(
                    selected = isSelected,
                    onClick = { 
                        selectedTab = tab
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
        },
        layoutType = navigationSuiteType,
        modifier = modifier
    ) {
        // Display selected tab content
        // Note: Can't use CurrentTab() because it crashes when DetailScreen is on stack
        selectedTab.Content()
    }
}
