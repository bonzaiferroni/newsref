package streetlight.app.ui.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.io.EventDao
import streetlight.app.ui.core.DataList
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.dto.EventInfo

@Composable
fun EventListScreen() {
    val navigator = rememberNavigator()
    val viewModel = koinViewModel(EventListModel::class)
    val state by viewModel.state
    DataList(
        title = "Events",
        items = state.items,
        provideName = { "${it.locationName} (${it.areaName})" },
        floatingAction = { navigator.navigate("/createEvent") },
        navigator = navigator,
    )
}

class EventListModel(
    private val eventDao: EventDao,
) : UiModel<EventListState>(EventListState()) {
    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = eventDao.getAllInfo()
            sv = sv.copy(items = items)
        }
    }
}

data class EventListState(
    val items: List<EventInfo> = emptyList(),
) : UiState