package newsref.app.ui

import androidx.compose.runtime.*
import newsref.app.*
import pondui.ui.controls.*
import pondui.ui.nav.*

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