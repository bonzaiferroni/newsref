package streetlight.web.ui.models

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import streetlight.model.core.Request
import streetlight.model.core.Song
import streetlight.model.dto.EventInfo
import streetlight.web.core.ViewModel
import streetlight.web.io.stores.EventStore
import streetlight.web.io.stores.RequestStore
import streetlight.web.io.stores.SongStore

class EventProfileModel(
    private val eventId: Int,
    private val eventStore: EventStore = EventStore(),
    private val songStore: SongStore = SongStore(),
    private val requestStore: RequestStore = RequestStore(),
) : ViewModel() {
    private val _eventStream = MutableStateFlow(EventInfo())
    val eventStream = _eventStream.asStateFlow()
    private val _songStream = MutableStateFlow<List<Song>>(emptyList())
    val songStream = _songStream.asStateFlow()

    init {
        viewModelScope.launch {
            _songStream.value = songStore.getAll()
        }
        viewModelScope.launch {
            while (true) {
                try {
                    refreshEvents()
                } catch (e: Exception) {
                    console.log(e.message)
                }
                delay(5000)
            }
        }
    }

    private suspend fun refreshEvents() {
        _eventStream.value = eventStore.getInfo(this.eventId)
    }

    suspend fun makeRequest(song: Song) {
        val request = Request(eventId = eventId, songId = song.id)
        val result = requestStore.create(request)
        if (result > 0) {
            refreshEvents()
        }
    }
}