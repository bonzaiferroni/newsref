package streetlight.app.ui.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.core.parameter.parametersOf
import streetlight.app.Scenes
import streetlight.app.io.EventDao
import streetlight.app.io.SongDao
import streetlight.app.io.RequestDao
import streetlight.app.services.BusService
import streetlight.app.ui.debug.controls.DataEditor
import streetlight.app.ui.debug.controls.DataMenu
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.app.ui.debug.controls.StringField
import streetlight.model.dto.EventInfo
import streetlight.model.Event
import streetlight.model.Song
import streetlight.model.Request

@Composable
fun RequestEditorScreen(id: Int?, navigator: Navigator?) {
    val viewModel = koinViewModel<RequestEditorModel> { parametersOf(id) }
    val state by viewModel.state

    DataEditor(
        title = "Add Request",
        result = state.result,
        isComplete = state.isComplete,
        isCreate = id == null,
        createData = viewModel::createRequest,
        navigator = navigator,
    ) {
        DataMenu(
            item = state.events.find { it.id == state.request.eventId },
            items = state.events,
            getName = { it.locationName },
            updateItem = viewModel::updateEvent,
        ) {
            viewModel.requestEvent()
            Scenes.eventEditor.go(navigator)
        }
        DataMenu(
            item = state.songs.find { it.id == state.request.songId },
            items = state.songs,
            getName = { it.name },
            updateItem = viewModel::updatePerformance,
        ) {
            viewModel.requestPerformance()
            Scenes.songEditor.go(navigator)
        }
        StringField(
            label = "Notes",
            value = state.request.notes,
            onValueChange = viewModel::updateNotes
        )
    }
}

class RequestEditorModel(
    private val id: Int?,
    private val requestDao: RequestDao,
    private val eventDao: EventDao,
    private val songDao: SongDao,
    private val bus: BusService
) : UiModel<RequestEditorState>(RequestEditorState()) {

    init {
        if (id != null) {
            viewModelScope.launch(Dispatchers.IO) {
                requestDao.get(id)?.let { sv = sv.copy(request = it) }
            }
        }
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            val events = eventDao.getAllInfo()
            val performances = songDao.getAll()
            sv = sv.copy(events = events, songs = performances)
        }
    }

    fun createRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            if (id == null) {
                val id = requestDao.create(sv.request)
                val isFinished = id > 0
                sv = sv.copy(isComplete = isFinished, result = "id: $id")
            } else {
                val isComplete = requestDao.update(sv.request)
                sv = sv.copy(isComplete = isComplete, result = "update: $isComplete")
            }
            bus.supply(sv.request)
        }
    }

    fun updateEvent(eventInfo: EventInfo) {
        sv = sv.copy(request = sv.request.copy(eventId = eventInfo.id))
    }

    fun requestEvent() {
        bus.request<Event> {
            sv = sv.copy(request = sv.request.copy(eventId = it.id))
            fetchData()
        }
    }

    fun updatePerformance(song: Song) {
        sv = sv.copy(request = sv.request.copy(songId = song.id))
    }

    fun requestPerformance() {
        bus.request<Song> {
            sv = sv.copy(request = sv.request.copy(songId = it.id))
            fetchData()
        }
    }

    fun updateNotes(value: String) {
        sv = sv.copy(request = sv.request.copy(notes = value))
    }
}

data class RequestEditorState(
    val request: Request = Request(),
    val isComplete: Boolean = false,
    val events: List<EventInfo> = emptyList(),
    val songs: List<Song> = emptyList(),
    val result: String = ""
) : UiState