package streetlight.app.ui.event

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import streetlight.app.data.EventDao
import streetlight.app.data.LocationDao
import streetlight.app.ui.abstract.UiModel
import streetlight.app.ui.abstract.UiState
import streetlight.model.Event
import streetlight.model.Location

class CreateEventModel(
    private val eventDao: EventDao,
    private val locationDao: LocationDao,
) : UiModel<CreateEventState>(CreateEventState()) {

    fun updateStartTime(instant: Instant) {
        sv = sv.copy(
            event = sv.event.copy(
                startTime = instant.epochSeconds,
            )
        )
    }

    fun updateEndTime(instant: Instant) {
        sv = sv.copy(
            event = sv.event.copy(
                endTime = instant.epochSeconds,
            )
        )
    }

    fun updateLocation(location: Location) {
        sv = sv.copy(
            event = sv.event.copy(
                locationId = location.id,
            ),
            locations = listOf(location)
        )
    }

    fun updateSearch(search: String) {
        sv = sv.copy(search = search)
        screenModelScope.launch(Dispatchers.IO) {
            val locations = locationDao.search(search)
            sv = sv.copy(
                locations = locations,
                event = sv.event.copy(
                    locationId = locations.firstOrNull()?.id ?: 0
                )
            )
        }
    }

    fun fetchLocations() {
        screenModelScope.launch(Dispatchers.IO) {
            val locations = locationDao.getAll()
            sv = sv.copy(locations = locations)
        }
    }

    fun addEvent() {
        screenModelScope.launch(Dispatchers.IO) {
            val id = eventDao.create(sv.event)
            val isFinished = id > 0
            sv = sv.copy(
                result = "result: $id",
                event = sv.event.copy(id = id),
                isFinished = isFinished
            )
        }
    }
}

data class CreateEventState(
    val event: Event = Event(),
    val isFinished: Boolean = false,
    val search: String = "",
    val locations: List<Location> = emptyList(),
    val result: String = ""
) : UiState