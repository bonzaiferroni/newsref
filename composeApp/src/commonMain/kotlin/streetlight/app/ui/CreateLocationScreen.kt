package streetlight.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import streetlight.app.chopui.Scaffold

class CreateLocationScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<CreateLocationModel>()
        val state by screenModel.state
        Scaffold("Add Location", navigator) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    TextField(
                        value = state.location.name,
                        onValueChange = screenModel::updateName,
                        label = { Text("Name") }
                    )
                    TextField(
                        value = state.location.latitude.toString(),
                        onValueChange = screenModel::updateLatitude,
                        label = { Text("Latitude") }
                    )
                    TextField(
                        value = state.location.longitude.toString(),
                        onValueChange = screenModel::updateLongitude,
                        label = { Text("Longitude") }
                    )
                    Button(onClick = screenModel::addLocation) {
                        Text("Add Location")
                    }
                    Text(state.result)
                }
            }
        }
    }
}