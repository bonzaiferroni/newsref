package newsref.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.*
import newsref.app.*
import newsref.app.blip.controls.Button
import newsref.app.blip.controls.Text
import newsref.app.generated.resources.Res
import newsref.app.generated.resources.compose_multiplatform
import newsref.app.blip.nav.LocalNav
import newsref.app.blip.nav.Scaffold
import newsref.app.blip.theme.Blip
import org.jetbrains.compose.resources.painterResource

@Composable
fun StartScreen(route: StartRoute) {
    var showContent by remember { mutableStateOf(false) }
    val nav = LocalNav.current
    Scaffold(false, transition = slideInHorizontally { it }) {
        Button({ nav.go(HelloRoute) }) {
            Text("Go to Hello")
        }
        Button({ showContent = !showContent }) {
            Text("Click me!")
        }
        AnimatedVisibility(showContent) {
            val greeting = remember { Greeting().greet() }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                BasicText("Compose: $greeting")
            }
        }
    }
}