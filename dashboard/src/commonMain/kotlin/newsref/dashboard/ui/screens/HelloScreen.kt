package newsref.dashboard.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import newsref.dashboard.Greeting
import newsref.dashboard.HelloRoute
import newsref.dashboard.LocalNavigator
import newsref.dashboard.ScreenRoute
import newsref.dashboard.StartRoute
import newsref.dashboard.generated.resources.Res
import newsref.dashboard.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

@Composable
fun HelloScreen(
    route: HelloRoute,
    viewModel: HelloModel = viewModel { HelloModel(route) },
) {
    val uiState by viewModel.state.collectAsState()
    val nav = LocalNavigator.current
    var showContent by remember { mutableStateOf(false) }
    Column {
        TextField(value = uiState.name, onValueChange = viewModel::changeName)
        Button(onClick = { nav.go(StartRoute())}) {
            Text("Go to Start")
        }
        Button(onClick = { showContent = !showContent }) {
            Text("Click me!")
        }
        AnimatedVisibility(showContent) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Text("Hello ${uiState.name}!")
            }
        }
    }
}

class HelloModel(route: HelloRoute) : StateModel<HelloState>(HelloState(route.name)) {

    fun changeName(newName: String) {
        setState { it.copy(name = newName) }
    }
}

data class HelloState(
    val name: String
)