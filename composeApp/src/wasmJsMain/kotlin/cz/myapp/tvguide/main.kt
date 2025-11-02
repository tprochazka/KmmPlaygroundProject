package cz.myapp.tvguide

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

// Unified Wasm/web entry point.
@OptIn(ExperimentalComposeUiApi::class)
fun main() = ComposeViewport { App() }