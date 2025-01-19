package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import newsref.dashboard.HelloRoute
import newsref.dashboard.ScreenRoute
import newsref.dashboard.StartRoute

@Composable
fun HelloScreen(
    route: HelloRoute,
    navController: NavHostController,
    viewModel: HelloModel = viewModel { HelloModel(route) },
) {
    val uiState by viewModel.state.collectAsState()
    Column {
        TextField(value = uiState.name, onValueChange = viewModel::changeName)
        Text("Hello ${uiState.name}!")
        Button(onClick = { navController.navigate(StartRoute)}) {
            Text("Go to Start")
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