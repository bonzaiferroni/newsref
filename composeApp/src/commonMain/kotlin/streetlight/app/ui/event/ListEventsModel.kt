package streetlight.app.ui.event

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.data.LocationDao
import streetlight.app.data.EventDao
import streetlight.app.ui.abstract.UiModel
import streetlight.app.ui.abstract.UiState
import streetlight.dto.EventInfo
import streetlight.model.Location
import streetlight.model.Event

class EventListModel(
    private val eventDao: EventDao,
) : UiModel<EventListState>(EventListState()) {
    init {
        fetchEvents()
    }

    fun fetchEvents() {
        screenModelScope.launch(Dispatchers.IO) {
            val events = eventDao.getAllInfo()
            sv = sv.copy(events = events)
        }
    }
}

data class EventListState(
    val events: List<EventInfo> = emptyList(),
) : UiState