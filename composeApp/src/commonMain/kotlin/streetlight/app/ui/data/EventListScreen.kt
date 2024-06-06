package streetlight.app.ui.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.io.EventDao
import streetlight.app.ui.core.DataList
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.dto.EventInfo

class EventListScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<EventListModel>()
        val state by screenModel.state
        DataList(
            title = "Events",
            items = state.items,
            provideName = { "${it.locationName} (${it.areaName})" },
            floatingAction = { navigator?.push(EventCreatorScreen { screenModel.refresh() }) },
            navigator = navigator,
        )
    }
}

class EventListModel(
    private val eventDao: EventDao,
) : UiModel<EventListState>(EventListState()) {
    init {
        refresh()
    }

    fun refresh() {
        screenModelScope.launch(Dispatchers.IO) {
            val items = eventDao.getAllInfo()
            sv = sv.copy(items = items)
        }
    }
}

data class EventListState(
    val items: List<EventInfo> = emptyList(),
) : UiState