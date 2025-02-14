package newsref.app.ui

import androidx.compose.runtime.*
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.nav.*

@Composable
fun HelloScreen(route: HelloRoute) {
    val nav = LocalNav.current
    Text("Just wanted to say hi!")
    Button({ nav.go(StartRoute) }) {
        Text("Go to Start")
    }
}