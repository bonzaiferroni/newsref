package newsref.app.ui

import androidx.compose.runtime.*
import newsref.app.*
import io.pondlib.compose.ui.controls.*
import io.pondlib.compose.ui.nav.*

@Composable
fun HelloScreen(route: HelloRoute) {
    Scaffold {
        val nav = LocalNav.current
        Text("Just wanted to say hi!")
        Button({ nav.go(StartRoute) }) {
            Text("Go to Start")
        }
    }
}