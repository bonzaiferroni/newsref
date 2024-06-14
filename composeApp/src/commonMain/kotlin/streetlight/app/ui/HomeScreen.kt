@file:OptIn(ExperimentalResourceApi::class, ExperimentalResourceApi::class)

package streetlight.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import streetlight.app.chopui.Scaffold
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import streetlight.app.chopui.BoxScaffold
import streetlight.composeapp.generated.resources.Res
import streetlight.composeapp.generated.resources.compose_multiplatform

@Composable
fun HomeScreen(navigator: Navigator) {
    val screenModel = koinViewModel(HomeModel::class)
    val state by screenModel.state

    BoxScaffold {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("counter: ${state.counter}")
            Button(onClick = screenModel::growCounter) {
                Text("grow")
            }
            Button(onClick = { navigator.navigate("/user") }) {
                Text("Create User")
            }
            Button(onClick = { navigator.navigate("/login") }) {
                Text("Login")
            }
            Button(onClick = { navigator.navigate("/locations") }) {
                Text("Locations")
            }
            Button(onClick = { navigator.navigate("/areas") }) {
                Text("Areas")
            }
            Button(onClick = { navigator.navigate("/events") }) {
                Text("Events")
            }
            Button(onClick = { navigator.navigate("/performances") }) {
                Text("Performances")
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GreetingContent() {
    var showContent by remember { mutableStateOf(false) }
    Button(onClick = { showContent = !showContent }) {
        Text("Click me!")
    }
    AnimatedVisibility(showContent) {
        val greeting = remember { "yo!" }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painterResource(Res.drawable.compose_multiplatform), null)
            Text("Compose: $greeting")
        }
    }
}