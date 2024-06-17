package streetlight.app.ui.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.Scenes
import streetlight.app.io.RequestDao
import streetlight.app.services.BusService
import streetlight.app.ui.core.DataList
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.dto.RequestInfo
import streetlight.model.Request

@Composable
fun RequestListScreen(navigator: Navigator?) {
    val viewModel = koinViewModel<RequestListModel>()
    val state by viewModel.state

    DataList(
        title = "Requests",
        items = state.requests,
        provideName = { "${it.performanceName} (${it.locationName})" },
        floatingAction = {
            viewModel.onNewRequest()
            Scenes.requestEditor.go(navigator)
        },
        navigator = navigator,
        onClick = {
            Scenes.requestEditor.go(navigator, it.id)
        }
    )
}

class RequestListModel(
    private val requestDao: RequestDao,
    private val bus: BusService,
) : UiModel<RequestListState>(RequestListState()) {
    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val requests = requestDao.getAllInfo()
            sv = sv.copy(requests = requests)
        }
    }

    fun onNewRequest() {
        bus.request<Request> {
            refresh()
        }
    }
}

data class RequestListState(
    val requests: List<RequestInfo> = emptyList(),
    val result: String = "",
) : UiState