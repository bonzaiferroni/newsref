import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import streetlight.app.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "streetlight") {
        App()
    }
}