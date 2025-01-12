package newsref.dashboard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import newsref.dashboard.AppScreen

@Composable
fun HelloScreen(
    navController: NavHostController,
    viewModel: HelloModel = viewModel { HelloModel() },
) {
    val uiState by viewModel.uiState.collectAsState()
    Column {
        TextField(value = uiState.name, onValueChange = viewModel::changeName)
        Text("Hello ${uiState.name}!")
        Button(onClick = { navController.navigate(AppScreen.Start.name)}) {
            Text("Go to Start")
        }
    }
}

class HelloModel : ViewModel() {
    private val _uiState = MutableStateFlow(HelloState("cupcake"))
    val uiState: StateFlow<HelloState> = _uiState.asStateFlow()

    fun changeName(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }
}

data class HelloState(
    val name: String
)