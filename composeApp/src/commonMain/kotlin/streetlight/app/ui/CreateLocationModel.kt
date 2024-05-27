package streetlight.app.ui

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.data.LocationDao
import streetlight.app.ui.model.UiModel
import streetlight.app.ui.model.UiState
import streetlight.model.Location
import streetlight.model.User

class CreateLocationModel(
    private val locationDao: LocationDao,
) : UiModel<CreateLocationState>(CreateLocationState()) {
    
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

    fun addLocation() {
        screenModelScope.launch(Dispatchers.IO) {
            val result = locationDao.addLocation(sv.location)
            sv = sv.copy(result = result, location = Location())
        }
    }
}

data class CreateLocationState(
    val location: Location = Location(),
    val result: String = ""
) : UiState