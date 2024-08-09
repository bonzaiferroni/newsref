package streetlight.app.ui.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.io.EventDao
import streetlight.app.io.LocationDao
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Event
import streetlight.model.Location
import streetlight.model.dto.EventInfo

class NowModel(
    private val eventDao: EventDao,
    private val locationDao: LocationDao,
) : UiModel<NowState>(NowState()) {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val events = eventDao.getAllCurrentInfos()
            sv = sv.copy(infos = events)
        }
    }

    fun toggleNewEvent() {
        if (sv.addingEvent) {
            sv = sv.copy(addingEvent = false)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val locations = locationDao.getAll()
                sv = sv.copy(
                    addingEvent = true,
                    locations = locations,
                    chosenLocation = locations.firstOrNull()
                )
            }
        }
    }

    fun chooseLocation(location: Location?) {
        sv = sv.copy(chosenLocation = location)
    }

    fun addEvent() {
        val location = sv.chosenLocation ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val event = Event(
                userId = 1,
                locationId = location.id,
            )
            eventDao.create(event)
            val events = eventDao.getAllInfo()
            sv = sv.copy(infos = events, addingEvent = false)
        }
    }
}

data class NowState(
    val infos: List<EventInfo> = emptyList(),
    val locations: List<Location> = emptyList(),
    val addingEvent: Boolean = false,
    val chosenLocation: Location? = null,
) : UiState