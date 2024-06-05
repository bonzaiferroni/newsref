package streetlight.app.ui.location

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.data.AreaDao
import streetlight.app.data.LocationDao
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Area
import streetlight.model.Location

class LocationCreateModel(
    private val areaDao: AreaDao,
    private val locationDao: LocationDao,
) : UiModel<CreateLocationState>(CreateLocationState()) {

    init {
        fetchAreas()
    }

    fun fetchAreas() {
        screenModelScope.launch(Dispatchers.IO) {
            val areas = areaDao.getAll()
            sv = sv.copy(areas = areas)
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

    fun addLocation() {
        screenModelScope.launch(Dispatchers.IO) {
            val id = locationDao.addLocation(sv.location)
            sv = sv.copy(
                result = "$id",
                location = sv.location.copy(id = id),
                isFinished = id > 0
            )
        }
    }
}

data class CreateLocationState(
    val location: Location = Location(),
    val areas: List<Area> = emptyList(),
    val isFinished: Boolean = false,
    val result: String = "",
    val latitude: String = "",
    val longitude: String = ""
) : UiState