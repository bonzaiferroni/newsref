package streetlight.app.ui.main

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import streetlight.model.Request

@Composable
fun EventProfileScreen(id: Int, navigator: Navigator?) {
    val screenModel = koinViewModel<EventProfileModel> { parametersOf(id) }
    val state by screenModel.state

    BoxScaffold(
        title = "Event: ${state.event.locationName}",
        navigator = navigator,
    ) {
        LazyColumn {
            items(state.requests) { request ->
                Row {
                    Text(request.performanceName)
                    Spacer(Modifier.weight(1f))
                    Switch(
                        checked = request.performed,
                        onCheckedChange = { screenModel.updatePerformed(request.id, it) }
                    )
                }
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
            refresh()
        }
    }

    fun updatePerformed(requestId: Int, performed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = sv.requests
                .find { it.id == requestId }
                ?.let { Request(it.id, it.eventId, it.performanceId, it.time, performed) }
                ?: return@launch

            requestDao.update(request)
            refresh()
        }
    }

    private suspend fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
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