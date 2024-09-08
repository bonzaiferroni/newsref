package streetlight.app.ui.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.Scenes
import streetlight.app.io.AreaDao
import streetlight.app.io.LocationDao
import streetlight.app.services.BusService
import streetlight.app.ui.debug.controls.DataList
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.core.Area
import streetlight.model.core.Location

@Composable
fun LocationListScreen(navigator: Navigator?) {
    val viewModel = koinViewModel(LocationListModel::class)
    val state by viewModel.state

    DataList(
        title = "Locations",
        items = state.locations,
        provideName = { "${it.location.name} (${it.area.name})" },
        floatingAction = {
            viewModel.onNewLocation()
            Scenes.locationEditor.go(navigator)
        },
        navigator = navigator,
        onEdit = {
            Scenes.locationEditor.go(navigator, it.location.id)
        },
        onDelete = viewModel::deleteLocation,
    )
}

class LocationListModel(
    private val locationDao: LocationDao,
    private val areaDao: AreaDao,
    private val bus: BusService
) : UiModel<LocationListState>(LocationListState()) {
    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val locations = locationDao.getAll()
            val areas = areaDao.getAll()
            val infos = locations.map { location ->
                val area = areas.find { it.id == location.areaId } ?: Area()
                LocationInfo(location, area)
            }
            sv = sv.copy(locations = infos)
        }
    }

    fun onNewLocation() {
        bus.request<Location> {
            refresh()
        }
    }

    fun deleteLocation(location: LocationInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            locationDao.delete(location.location.id)
            refresh()
        }
    }
}

data class LocationListState(
    val locations: List<LocationInfo> = emptyList(),
) : UiState

data class LocationInfo(
    val location: Location,
    val area: Area,
)