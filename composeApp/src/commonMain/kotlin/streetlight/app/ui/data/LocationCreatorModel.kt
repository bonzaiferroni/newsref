package streetlight.app.ui.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.io.AreaDao
import streetlight.app.io.LocationDao
import streetlight.app.services.BusService
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Area
import streetlight.model.Location

class LocationCreatorModel(
    private val areaDao: AreaDao,
    private val locationDao: LocationDao,
    private val bus: BusService,
) : UiModel<CreateLocationState>(CreateLocationState()) {

    init {
        fetchAreas()
    }

    private fun fetchAreas() {
        viewModelScope.launch(Dispatchers.IO) {
            val areas = areaDao.getAll()
            sv = sv.copy(areas = areas)
        }
    }

    fun onNewArea() {
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

    fun updateArea(id: Int) {
        sv = sv.copy(location = sv.location.copy(areaId = id))
    }

    fun createLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = locationDao.addLocation(sv.location)
            sv = sv.copy(
                result = "$id",
                location = sv.location.copy(id = id),
                isComplete = id > 0
            )
            bus.supply(sv.location)
        }
    }
}

data class CreateLocationState(
    val location: Location = Location(),
    val areas: List<Area> = emptyList(),
    val isComplete: Boolean = false,
    val result: String = "",
    val latitude: String = "",
    val longitude: String = ""
) : UiState