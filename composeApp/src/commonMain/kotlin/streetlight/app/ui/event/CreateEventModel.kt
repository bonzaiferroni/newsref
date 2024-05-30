package streetlight.app.ui.event

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import streetlight.app.data.EventDao
import streetlight.app.data.LocationDao
import streetlight.app.ui.abstract.UiModel
import streetlight.app.ui.abstract.UiState
import streetlight.model.Event
import streetlight.model.Location
import streetlight.utils.toEpochSeconds
import streetlight.utils.toLocalEpochSeconds

class CreateEventModel(
    private val eventDao: EventDao,
    private val locationDao: LocationDao,
) : UiModel<CreateEventState>(CreateEventState()) {

    fun updateStartTime(dateTime: LocalDateTime) {
        sv = sv.copy(event = sv.event.copy(timeStart = dateTime.toEpochSeconds()))
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

    fun updateDuration(duration: String) {
        val hours = durationOptions[duration] ?: return
        sv = sv.copy(
            event = sv.event.copy(
                hours = hours
            ),
            duration = duration
        )
    }
}

data class CreateEventState(
    val event: Event = Event(
        timeStart = Clock.System.now().toLocalEpochSeconds(),
        hours = 1f,
    ),
    val isFinished: Boolean = false,
    val search: String = "",
    val locations: List<Location> = emptyList(),
    val result: String = "",
    val duration: String = defaultDuration
) : UiState

val defaultDuration = "1 hour"

val durationOptions = mapOf(
    "15 minutes" to 15 / 60f,
    "30 minutes" to 30 / 60f,
    "1 hour" to 1f,
    "2 hours" to 2f,
    "3 hours" to 3f,
    "4 hours" to 4f
)