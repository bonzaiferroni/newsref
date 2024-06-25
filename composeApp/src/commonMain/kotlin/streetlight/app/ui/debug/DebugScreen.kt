@file:OptIn(ExperimentalResourceApi::class, ExperimentalResourceApi::class)

package streetlight.app.ui.debug

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
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import streetlight.app.Scenes
import streetlight.app.chopui.BoxScaffold
import streetlight.composeapp.generated.resources.Res
import streetlight.composeapp.generated.resources.compose_multiplatform

@Composable
fun DebugScreen(navigator: Navigator) {
    val screenModel = koinViewModel(DebugModel::class)
    val state by screenModel.state

    BoxScaffold(navigator = navigator) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("counter: ${state.counter}")
            Button(onClick = screenModel::growCounter) {
                Text("grow")
            }

            // from Navigation.kt
            rootScenes.forEach {
                Button(onClick = { navigator.navigate(it.route) }) {
                    Text(it.name)
                }
            }
        }
    }
}

val rootScenes = listOf(
    Scenes.userEditor,
    Scenes.locationList,
    Scenes.areaList,
    Scenes.eventList,
    Scenes.performanceList,
    Scenes.requestList
)

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