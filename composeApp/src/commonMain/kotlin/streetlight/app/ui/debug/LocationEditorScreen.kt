package streetlight.app.ui.debug

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.core.parameter.parametersOf
import streetlight.app.Scenes
import streetlight.app.io.AreaDao
import streetlight.app.io.LocationDao
import streetlight.app.services.BusService
import streetlight.app.ui.debug.controls.DataEditor
import streetlight.app.ui.debug.controls.DataMenu
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
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
            item = state.areas.find { it.id == state.location.areaId },
            items = state.areas,
            getName = { it.name },
            updateItem = viewModel::updateArea
        ) {
            viewModel.requestArea()
            Scenes.areaEditor.go(navigator)
        }
    }
}

class LocationEditorModel(
    private val id: Int?,
    private val areaDao: AreaDao,
    private val locationDao: LocationDao,
    private val bus: BusService,
) : UiModel<LocationEditorState>(LocationEditorState()) {

    init {
        if (id != null) {
            viewModelScope.launch(Dispatchers.IO) {
                locationDao.get(id)?.let {
                    sv = sv.copy(
                        location = it,
                        latitude = it.latitude.toString(),
                        longitude = it.longitude.toString()
                    )
                }
            }
        }
        fetchAreas()
    }

    private fun fetchAreas() {
        viewModelScope.launch(Dispatchers.IO) {
            val areas = areaDao.getAll()
            sv = sv.copy(areas = areas)
        }
    }

    fun requestArea() {
        bus.request<Area> {
            sv = sv.copy(location = sv.location.copy(areaId = it.id))
            fetchAreas()
        }
    }

    fun updateName(name: String) {
        sv = sv.copy(location = sv.location.copy(name = name))
    }

    fun updateLatitude(value: String) {
        val latitude = value.toDoubleOrNull() ?: sv.location.latitude
        sv = sv.copy(
            location = sv.location.copy(latitude = latitude),
            latitude = value
        )
    }

    fun updateLongitude(value: String) {
        val longitude = value.toDoubleOrNull() ?: sv.location.longitude
        sv = sv.copy(
            location = sv.location.copy(longitude = longitude),
            longitude = value
        )
    }

    fun updateArea(area: Area) {
        sv = sv.copy(location = sv.location.copy(areaId = area.id))
    }

    fun createLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            if (sv.location.id == 0) {
                val id = locationDao.create(sv.location)
                sv = sv.copy(
                    result = "$id",
                    location = sv.location.copy(id = id),
                    isComplete = id > 0
                )
            } else {
                val isComplete = locationDao.update(sv.location)
                sv = sv.copy(
                    result = "$isComplete",
                    isComplete = isComplete
                )
            }
            bus.supply(sv.location)
        }
    }
}

data class LocationEditorState(
    val location: Location = Location(),
    val areas: List<Area> = emptyList(),
    val isComplete: Boolean = false,
    val result: String = "",
    val latitude: String = "0",
    val longitude: String = "0"
) : UiState