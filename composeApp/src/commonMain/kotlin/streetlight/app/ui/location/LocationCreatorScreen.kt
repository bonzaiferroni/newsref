package streetlight.app.ui.location

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import streetlight.app.ui.area.AreaCreatorScreen
import streetlight.app.ui.core.DataCreator
import streetlight.model.Area
import streetlight.model.Location

class LocationCreatorScreen(
    private val onComplete: ((Location) -> Unit)?
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<LocationCreatorModel>()
        val state by screenModel.state

        DataCreator(
            title = "Add Location",
            item = state.location,
            isComplete = state.isComplete,
            result = state.result,
            onComplete = onComplete,
            createData = screenModel::createLocation,
            navigator = navigator,
        ) {
            TextField(
                value = state.location.name,
                onValueChange = screenModel::updateName,
                label = { Text("Name") }
            )
            TextField(
                value = state.latitude,
                onValueChange = screenModel::updateLatitude,
                label = { Text("Latitude") }
            )
            TextField(
                value = state.longitude,
                onValueChange = screenModel::updateLongitude,
                label = { Text("Longitude") }
            )
            AreaChooser(
                navigator = navigator,
                location = state.location,
                areas = state.areas,
                updateArea = screenModel::updateArea,
                fetchAreas = screenModel::fetchAreas
            )
        }
    }

    @Composable
    fun AreaChooser(
        navigator: Navigator?,
        location: Location,
        areas: List<Area>,
        updateArea: (Int) -> Unit,
        fetchAreas: () -> Unit,
    ) {
        var expanded by remember { mutableStateOf(false) }
        val area = areas.find { it.id == location.areaId }

        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                area?.let {
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
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        navigator?.push(AreaCreatorScreen() {
                            updateArea(it.id)
                            fetchAreas()
                        })
                    },
                    text = { Text("New...") }
                )
                areas.forEach { area ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            updateArea(area.id)
                        },
                        text = { Text(area.name) }
                    )
                }
            }
        }
    }
}