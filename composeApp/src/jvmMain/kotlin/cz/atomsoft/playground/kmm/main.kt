package cz.atomsoft.playground.kmm

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KmmPlaygroundProject",
    ) {
        App()
    }
}