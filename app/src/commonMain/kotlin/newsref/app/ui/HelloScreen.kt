package newsref.app.ui

import androidx.compose.runtime.*
import newsref.app.*
import newsref.app.pond.controls.*
import newsref.app.pond.nav.*

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