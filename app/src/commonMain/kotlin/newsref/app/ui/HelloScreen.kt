package newsref.app.ui

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import newsref.app.*
import newsref.app.fui.Button
import newsref.app.nav.LocalNav

@Composable
fun HelloScreen(route: HelloRoute) {
    val nav = LocalNav.current
    BasicText("Just wanted to say hi!")
    Button({ nav.go(StartRoute) }) {
        BasicText("Go to Start")
    }
}