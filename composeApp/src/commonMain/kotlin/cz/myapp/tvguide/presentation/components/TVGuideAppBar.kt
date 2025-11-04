package cz.myapp.tvguide.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Standard top app bar for TV Guide screens.
 * 
 * Features:
 * - Title display
 * - Optional overflow menu with Settings
 * - Optional custom actions
 * - Optional back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TVGuideAppBar(
    title: String,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    showOverflowMenu: Boolean = true,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (showBackButton && onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Zpět"
                    )
                }
            }
        },
        actions = {
            // Custom actions (e.g., Favorites button in EPG)
            actions()
            
            // Overflow menu with Settings
            if (showOverflowMenu) {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Další možnosti"
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Nastavení") },
                        onClick = {
                            showMenu = false
                            onSettingsClick()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier
    )
}
