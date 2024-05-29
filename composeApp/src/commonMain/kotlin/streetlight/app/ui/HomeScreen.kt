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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import streetlight.app.chopui.Scaffold
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import streetlight.app.ui.area.AreaListScreen
import streetlight.app.ui.event.CreateEventScreen
import streetlight.app.ui.event.EventListScreen
import streetlight.app.ui.location.CreateLocationScreen
import streetlight.app.ui.location.LocationListScreen
import streetlight.app.ui.login.LoginScreen
import streetlight.app.ui.login.LoginState
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
                Text("counter: ${state.counter}")
                Button(onClick = screenModel::growCounter) {
                    Text("grow")
                }
                Button(onClick = { navigator.push(CreateUserScreen()) }) {
                    Text("Create User")
                }
                Button(onClick = { navigator.push(LoginScreen())}) {
                    Text("Login")
                }
                Button(onClick = { navigator.push(LocationListScreen()) }) {
                    Text("Locations")
                }
                Button(onClick = { navigator.push(AreaListScreen()) }) {
                    Text("Areas")
                }
                Button(onClick = { navigator.push(EventListScreen())}) {
                    Text("Events")
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