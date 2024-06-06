package streetlight.app.ui.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.io.AreaDao
import streetlight.app.io.LocationDao
import streetlight.app.ui.core.DataList
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Area
import streetlight.model.Location

class LocationListScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<LocationListModel>()
        val state by screenModel.state

        DataList(
            title = "Locations",
            items = state.locations,
            provideName = { "${it.location.name} (${it.area.name})" },
            floatingAction = { navigator?.push(LocationCreatorScreen() {
                screenModel.refresh()
            }) },
            navigator = navigator,
        )
    }

}

class LocationListModel(
    private val locationDao: LocationDao,
    private val areaDao: AreaDao
) : UiModel<LocationListState>(LocationListState()) {
    init {
        refresh()
    }

    fun refresh() {
        screenModelScope.launch(Dispatchers.IO) {
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