package streetlight.app.ui.main

import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.io.ApiClient
import streetlight.app.io.EventDao
import streetlight.app.io.RequestDao
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Event
import streetlight.model.EventStatus
import streetlight.model.Request
import streetlight.model.dto.EventInfo
import streetlight.model.dto.ImageUploadRequest
import streetlight.model.dto.RequestInfo
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class EventProfileModel(
    private val id: Int,
    private val eventDao: EventDao,
    private val requestDao: RequestDao,
) : UiModel<EventProfileState>(EventProfileState()) {

    init {
        refreshEvent()
        loop()
    }

    private var nextTime: Long = 0

    private var event: Event
        get() = sv.info.event
        set(value) {
            sv = sv.copy(info = sv.info.copy(event = value))
        }

    fun updatePerformed(requestId: Int, performed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = sv.requests
                .find { it.id == requestId }
                ?.let { Request(it.id, it.eventId, it.songId, it.time, performed) }
                ?: return@launch

            requestDao.update(request)
            refreshSongs()
        }
    }

    fun progressEvent() {
        when (event.status) {
            EventStatus.Pending -> {
                updateStatus(EventStatus.Started, System.currentTimeMillis(), null)
            }

            EventStatus.Started -> {
                val hours =
                    (System.currentTimeMillis() - event.timeStart) / 1000 / 60f / 60f
                updateStatus(EventStatus.Finished, event.timeStart, hours)
            }

            EventStatus.Finished -> {
                updateStatus(EventStatus.Started, event.timeStart, event.hours)
            }
        }
    }

    private fun updateStatus(status: EventStatus, timeStart: Long, hours: Float?) {
        viewModelScope.launch(Dispatchers.IO) {
            event = event.copy(
                status = status,
                timeStart = timeStart,
                hours = hours
            )
            eventDao.update(event)
        }
    }

    private fun loop() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                if (System.currentTimeMillis() > nextTime) {
                    refreshSongs()
                }

                delay(10000)
            }
        }
    }

    private fun refreshEvent() {
        viewModelScope.launch(Dispatchers.IO) {
            val event = eventDao.getInfo(id)
            sv = sv.copy(
                info = event ?: EventInfo(),
                imageUrl = event?.event?.url?.toImageUrl()
            )
        }
    }

    private suspend fun refreshSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            val requests = requestDao.getQueue(id)
            val newRequest = requests.firstOrNull()
            sv = sv.copy(requests = requests, current = newRequest)
            nextTime = System.currentTimeMillis() + 10000
        }
    }

    fun updateUrl(url: String) {
        sv = sv.copy(
            info = sv.info.copy(event = event.copy(url = url)),
            imageUrl = url.toImageUrl()
        )
    }

    private fun String.toImageUrl() =
        this.takeIf { it.startsWith("http") } ?: "${ApiClient.baseAddress}/$this"

    @OptIn(ExperimentalEncodingApi::class)
    fun saveImage() {
        // save base64 string to server
        viewModelScope.launch(Dispatchers.IO) {
            val file = FileKit.pickFile(
                type = PickerType.Image,
                mode = PickerMode.Single,
                title = "Pick an image",
                // initialDirectory = "/custom/initial/path"
            ) ?: return@launch

            val result = eventDao.postImage(
                ImageUploadRequest(
                    eventId = event.id,
                    filename = file.name,
                    image = Base64.encode(file.readBytes())
                )
            )
            if (result) {
                refreshEvent()
            }
        }
    }
}

data class EventProfileState(
    val info: EventInfo = EventInfo(),
    val imageUrl: String? = null,
    val requests: List<RequestInfo> = emptyList(),
    val current: RequestInfo? = null,
) : UiState

fun EventStatus.getButtonText() = when (this) {
    EventStatus.Pending -> "Start"
    EventStatus.Started -> "Finish"
    EventStatus.Finished -> "Resume"
}