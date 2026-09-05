package cz.myapp.tvguide

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

/**
 * Desktop (JVM) entry point for TV Guide app.
 */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "TV Guide",
    ) {
        App()
    }
}
