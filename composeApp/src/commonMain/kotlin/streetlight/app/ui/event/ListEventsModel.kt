package streetlight.app.ui.event

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.data.LocationDao
import streetlight.app.data.EventDao
import streetlight.app.ui.abstract.UiModel
import streetlight.app.ui.abstract.UiState
import streetlight.model.Location
import streetlight.model.Event

class EventListModel(
    private val locationDao: LocationDao,
    private val eventDao: EventDao,
) : UiModel<EventListState>(EventListState()) {
    init {
        fetchEvents()
    }

    fun fetchEvents() {
        screenModelScope.launch(Dispatchers.IO) {
            val events = eventDao.getAll()
            val locations = locationDao.getAll()
            sv = sv.copy(events = events)
        }
    }
}

data class EventListState(
    val events: List<Event> = emptyList(),
) : UiState