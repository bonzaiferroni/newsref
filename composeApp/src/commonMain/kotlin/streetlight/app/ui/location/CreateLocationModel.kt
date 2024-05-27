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

class CreateLocationModel(
    private val areaDao: AreaDao,
    private val locationDao: LocationDao,
) : UiModel<CreateLocationState>(CreateLocationState()) {

    init {
        screenModelScope.launch(Dispatchers.IO) {
            val areas = areaDao.getAll()
            sv = sv.copy(areas = areas)
        }
    }

    fun updateName(name: String) {
        sv = sv.copy(location = sv.location.copy(name = name))
    }

    fun updateLatitude(latitude: String) {
        val number = latitude.toDoubleOrNull() ?: return
        sv = sv.copy(location = sv.location.copy(latitude = number))
    }

    fun updateLongitude(longitude: String) {
        val number = longitude.toDoubleOrNull() ?: return
        sv = sv.copy(location = sv.location.copy(longitude = number))
    }

    fun updateArea(area: Area) {
        sv = sv.copy(location = sv.location.copy(areaId = area.id))
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
    val result: String = ""
) : UiState