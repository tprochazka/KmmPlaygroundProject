package cz.myapp.tvguide.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * EPG screen placeholder - Will show program guide grid (US3)
 */
@Composable
fun EpgScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Program - TV průvodce",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
