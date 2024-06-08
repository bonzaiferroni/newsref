package streetlight.app.ui.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.io.AreaDao
import streetlight.app.io.LocationDao
import streetlight.app.ui.core.DataList
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Area
import streetlight.model.Location

@Composable
fun LocationListScreen() {
    val navigator = rememberNavigator()
    val viewModel = koinViewModel(LocationListModel::class)
    val state by viewModel.state

    DataList(
        title = "Locations",
        items = state.locations,
        provideName = { "${it.location.name} (${it.area.name})" },
        floatingAction = { navigator.navigate("/createLocation") },
        navigator = navigator,
    )
}

class LocationListModel(
    private val locationDao: LocationDao,
    private val areaDao: AreaDao
) : UiModel<LocationListState>(LocationListState()) {
    init {
        refresh()
    }

    fun refresh() {
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
}

data class LocationListState(
    val locations: List<LocationInfo> = emptyList(),
) : UiState

data class LocationInfo(
    val location: Location,
    val area: Area,
)