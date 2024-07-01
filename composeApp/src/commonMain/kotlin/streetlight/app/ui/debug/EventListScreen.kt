package streetlight.app.ui.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.Scenes
import streetlight.app.io.EventDao
import streetlight.app.services.BusService
import streetlight.app.ui.debug.controls.DataList
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.dto.EventInfo
import streetlight.model.Event

@Composable
fun EventListScreen(navigator: Navigator?) {
    val viewModel = koinViewModel(EventListModel::class)
    val state by viewModel.state
    DataList(
        title = "Events",
        items = state.items,
        provideName = { "${it.locationName} (${it.areaName})" },
        floatingAction = {
            viewModel.onNewEvent()
            Scenes.eventEditor.go(navigator)
        },
        navigator = navigator,
        onEdit = { Scenes.eventEditor.go(navigator, it.id) },
        onDelete = viewModel::deleteEvent,
    )
}

class EventListModel(
    private val eventDao: EventDao,
    private val bus: BusService,
) : UiModel<EventListState>(EventListState()) {
    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = eventDao.getAllInfo()
            sv = sv.copy(items = items)
        }
    }

    fun onNewEvent() {
        bus.request<Event> {
            refresh()
        }
    }

    fun deleteEvent(event: EventInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            eventDao.delete(event.id)
            refresh()
        }
    }
}

data class EventListState(
    val items: List<EventInfo> = emptyList(),
) : UiState