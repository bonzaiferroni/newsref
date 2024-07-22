package streetlight.app.ui.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.io.EventDao
import streetlight.app.io.RequestDao
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Request
import streetlight.model.dto.EventInfo
import streetlight.model.dto.RequestInfo


class EventProfileModel(
    private val id: Int,
    private val eventDao: EventDao,
    private val requestDao: RequestDao,
) : UiModel<EventProfileState>(EventProfileState()) {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val event = eventDao.getInfo(id) ?: return@launch
            sv = sv.copy(event = event)
            loop()
        }
    }

    private var nextTime: Long = 0

    fun updatePerformed(requestId: Int, performed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = sv.requests
                .find { it.id == requestId }
                ?.let { Request(it.id, it.eventId, it.songId, it.time, performed) }
                ?: return@launch

            requestDao.update(request)
            refresh()
        }
    }

    private suspend fun loop() {
        while (true) {
            if (System.currentTimeMillis() > nextTime) {
                refresh()
            }

            delay(1000)
        }
    }

    private suspend fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val requests = requestDao.getAllInfo(id)
            val newRequest = requests.firstOrNull { r -> !sv.requests.any{it.id == r.id}}
            sv = sv.copy(requests = requests, notification = newRequest?.songName)
            nextTime = System.currentTimeMillis() + 10000
        }
    }
}

data class EventProfileState(
    val event: EventInfo = EventInfo(),
    val requests: List<RequestInfo> = emptyList(),
    val result: String = "",
    val notification: String? = null,
) : UiState