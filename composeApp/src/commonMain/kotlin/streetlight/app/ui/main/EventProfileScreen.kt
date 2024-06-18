package streetlight.app.ui.main

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.core.parameter.parametersOf
import streetlight.app.chopui.BoxScaffold
import streetlight.app.io.EventDao
import streetlight.app.io.RequestDao
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.dto.EventInfo
import streetlight.dto.RequestInfo

@Composable
fun EventProfileScreen(id: Int, navigator: Navigator?) {
    val screenModel = koinViewModel<EventProfileModel> { parametersOf(id) }
    val state by screenModel.state

    BoxScaffold(
        title = "Event: ${state.event.locationName}",
    ) {
        LazyColumn {
            items(state.requests) { request ->
                Text(request.performanceName)
            }
        }
    }
}

class EventProfileModel(
    private val id: Int,
    private val eventDao: EventDao,
    private val requestDao: RequestDao,
) : UiModel<EventProfileState>(EventProfileState()) {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val event = eventDao.getInfo(id) ?: return@launch
            sv = sv.copy(event = event)

            val requests = requestDao.getAllInfo(id)
            sv = sv.copy(requests = requests)
        }
    }
}

data class EventProfileState(
    val event: EventInfo = EventInfo(),
    val requests: List<RequestInfo> = emptyList(),
    val result: String = "",
) : UiState