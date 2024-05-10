import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bollwerks.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "SqlDem") {
        App()
    }
}