package cz.myapp.tvguide.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * List screen placeholder - Will show searchable program/channel lists (US4)
 */
@Composable
fun ListScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Seznam - Vyhledávání",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
