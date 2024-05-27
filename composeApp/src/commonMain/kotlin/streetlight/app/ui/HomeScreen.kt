@file:OptIn(ExperimentalResourceApi::class, ExperimentalResourceApi::class)

package streetlight.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import streetlight.app.chopui.Scaffold
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import streetlight.composeapp.generated.resources.Res
import streetlight.composeapp.generated.resources.compose_multiplatform

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<HomeModel>()
        val state by screenModel.state

        Scaffold {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                ExtraScreenContent(navigator)
                // GreetingContent()
                Text("counter: ${state.counter}")
                Button(onClick = screenModel::growCounter) {
                    Text("grow")
                }
                Button(onClick = { navigator.push(FoodScreen()) }) {
                    Text("Food")
                }
                Text(state.message)
                Button(onClick = screenModel::fetchMessage) {
                    Text("fetch")
                }
                Button(onClick = { navigator.push(CreateUserScreen()) }) {
                    Text("Create User")
                }
                Button(onClick = { navigator.push(CreateLocationScreen()) }) {
                    Text("Create Location")
                }
                Button(onClick = { navigator.push(CreateAreaScreen()) }) {
                    Text("Create Area")
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

    @Composable
    fun ExtraScreenContent(navigator: Navigator) {
        Button(onClick = { navigator.push(ExtraScreen())}) {
            Text("Extra")
        }
    }
}