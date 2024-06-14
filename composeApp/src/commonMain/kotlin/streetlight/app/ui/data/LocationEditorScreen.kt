package streetlight.app.ui.data

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.parameter.parametersOf
import streetlight.app.ui.core.DataEditor
import streetlight.app.ui.core.DataMenu
import streetlight.model.Area
import streetlight.model.Location

@Composable
fun LocationEditorScreen(id: Int?, navigator: Navigator?) {
    val viewModel = koinViewModel(LocationEditorModel::class) { parametersOf(id) }
    val state by viewModel.state

    DataEditor(
        title = "Add Location",
        isComplete = state.isComplete,
        result = state.result,
        createData = viewModel::createLocation,
        isCreate = id == null,
        navigator = navigator,
    ) {
        TextField(
            value = state.location.name,
            onValueChange = viewModel::updateName,
            label = { Text("Name") }
        )
        TextField(
            value = state.latitude,
            onValueChange = viewModel::updateLatitude,
            label = { Text("Latitude") }
        )
        TextField(
            value = state.longitude,
            onValueChange = viewModel::updateLongitude,
            label = { Text("Longitude") }
        )
        DataMenu(
            navigator = navigator,
            item = state.areas.find { it.id == state.location.areaId },
            items = state.areas,
            newItemLink = "/area",
            getName = { it.name },
            updateLocation = viewModel::updateArea,
            onNewLocation = viewModel::onNewArea
        )
    }
}