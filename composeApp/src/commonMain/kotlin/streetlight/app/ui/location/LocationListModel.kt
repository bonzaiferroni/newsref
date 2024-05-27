package streetlight.app.ui.location

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.data.AreaDao
import streetlight.app.data.LocationDao
import streetlight.app.ui.abstract.UiModel
import streetlight.app.ui.abstract.UiState
import streetlight.model.Area
import streetlight.model.Location

class LocationListModel(
    private val areaDao: AreaDao,
    private val locationDao: LocationDao,
) : UiModel<LocationListState>(LocationListState()) {
    init {
        fetchLocations()
    }

    fun fetchLocations() {
        screenModelScope.launch(Dispatchers.IO) {
            val locations = locationDao.getAll()
            val areas = areaDao.getAll()
            sv = sv.copy(locations = locations, areas = areas)
        }
    }
}

data class LocationListState(
    val locations: List<Location> = emptyList(),
    val areas: List<Area> = emptyList(),
) : UiState