package streetlight.app.ui.main

import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
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
import streetlight.model.dto.ImageUploadRequest
import streetlight.model.dto.RequestInfo
import java.io.File
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class EventProfileModel(
    private val id: Int,
    private val eventDao: EventDao,
    private val requestDao: RequestDao,
) : UiModel<EventProfileState>(EventProfileState()) {

    init {
        refreshEvent()
    }

    private var nextTime: Long = 0

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
        when (sv.status) {
            EventStatus.Pending -> startEvent()
            EventStatus.Started -> finishEvent()
            EventStatus.Finished -> resumeEvent()
        }
    }

    private fun startEvent() {
        viewModelScope.launch(Dispatchers.IO) {
            sv = sv.copy(event = sv.event.copy(timeStart = System.currentTimeMillis()))
            val success = eventDao.update(sv.event)
            if (success) {
                sv = sv.copy(status = EventStatus.Started)
                loop()
            }
        }
    }

    private fun finishEvent() {
        viewModelScope.launch(Dispatchers.IO) {
            val hours = (System.currentTimeMillis() - sv.event.timeStart) / 1000 / 60f / 60f
            sv = sv.copy(event = sv.event.copy(hours = hours))
            val success = eventDao.update(sv.event)
            if (success) {
                sv = sv.copy(status = EventStatus.Finished)
            }
        }
    }

    private fun resumeEvent() {
        sv = sv.copy(status = EventStatus.Started)
    }

    private suspend fun loop() {
        while (true) {
            if (System.currentTimeMillis() > nextTime) {
                refreshSongs()
            }

            delay(10000)
        }
    }

    private fun refreshEvent() {
        viewModelScope.launch(Dispatchers.IO) {
            val event = eventDao.getInfo(id)
            sv = sv.copy(event = event ?: EventInfo())
        }
    }

    private suspend fun refreshSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            val requests = requestDao.getAllInfo(id)
            val newRequest = requests.firstOrNull { r -> !sv.requests.any{it.id == r.id}}
            sv = sv.copy(requests = requests, notification = newRequest?.songName)
            nextTime = System.currentTimeMillis() + 10000
        }
    }

    fun updateUrl(url: String) {
        sv = sv.copy(event = sv.event.copy(url = url))
    }

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
                    eventId = sv.event.id,
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
    val event: EventInfo = EventInfo(),
    val requests: List<RequestInfo> = emptyList(),
    val status: EventStatus = EventStatus.Pending,
    val notification: String? = null,
) : UiState

enum class EventStatus {
    Pending,
    Started,
    Finished,
}