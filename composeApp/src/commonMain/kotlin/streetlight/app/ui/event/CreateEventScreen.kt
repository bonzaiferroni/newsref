package streetlight.app.ui.event

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import streetlight.app.chopui.Scaffold
import streetlight.app.ui.location.CreateLocationScreen
import streetlight.model.Event
import streetlight.model.Location

class CreateEventScreen(
    private val onComplete: ((id: Int) -> Unit)? = null
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<CreateEventModel>()
        val state by screenModel.state

        LaunchedEffect(state.isFinished) {
            if (state.isFinished) {
                navigator?.pop()
                onComplete?.invoke(state.event.id)
            }
        }

        Scaffold("Add Event", navigator) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    // time picker
                    // time picker
                    TextField(
                        value = state.search,
                        onValueChange = screenModel::updateSearch,
                        label = { Text("Search") }
                    )
                    LocationChooser(
                        navigator,
                        state.event,
                        state.locations,
                        screenModel::updateLocation,
                    )
                    Button(onClick = screenModel::addEvent) {
                        Text("Add Event")
                    }
                    Text(state.result)
                }
            }
        }
    }

    @Composable
    fun LocationChooser(
        navigator: Navigator?,
        event: Event,
        locations: List<Location>,
        updateLocation: (Location) -> Unit,
    ) {
        var expanded by remember { mutableStateOf(false) }
        val location = locations.find { it.id == event.locationId }

        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                location?.let {
                    Text(it.name)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More"
                    )
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(onClick = {
                    expanded = false
                    navigator?.push(CreateLocationScreen() {
                        updateLocation(it)
                    })
                }) {
                    Text("New...")
                }
                locations.forEach { location ->
                    DropdownMenuItem(onClick = {
                        expanded = false
                        updateLocation(location)
                    }) {
                        Text(location.name)
                    }
                }
            }
        }
    }
}