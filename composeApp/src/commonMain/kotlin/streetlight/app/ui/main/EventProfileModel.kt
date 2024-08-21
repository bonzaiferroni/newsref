package streetlight.app.ui.main

import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
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
import streetlight.model.utils.toFormatString
import streetlight.model.utils.toLocalDateTime
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

    fun updatePerformed(requestId: Int, accepted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (accepted) {
                val request = sv.info.requests
                    .find { it.id == requestId }
                    ?.let { Request(it.id, it.eventId, it.songId, it.time, true, it.notes, it.requesterName) }
                    ?: return@launch
                requestDao.update(request)
                event = event.copy(currentRequestId = requestId)
                eventDao.update(event)

                if (sv.info.requests.count() <= 1) {
                    requestDao.getRandomRequest(event.id)
                }
            } else {
                requestDao.delete(requestId)
            }
            refreshProfile()
        }
    }

    fun progressEvent() {
        when (event.status) {
            EventStatus.Pending -> {
                updateStatus(EventStatus.Started, System.currentTimeMillis(), null)
            }

            EventStatus.Started -> {
                val hours = (System.currentTimeMillis() - event.timeStart) / 1000 / 60f / 60f
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
                    refreshProfile()
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

    private suspend fun refreshProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                nextTime = System.currentTimeMillis() + 10000
                val info = eventDao.getInfo(id) ?: return@launch
                sv = sv.copy(info = info)
            } catch (e: Exception) {
                sv = sv.copy(updateStatus = "Failed to refresh songs.")
            }
        }
    }

    fun updateUrl(url: String) {
        sv = sv.copy(
            info = sv.info.copy(event = event.copy(url = url)),
            imageUrl = url.toImageUrl()
        )
    }

    fun updateStreamUrl(url: String) {
        event = event.copy(streamUrl = url)
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

            sv = sv.copy(updateStatus = "Uploading image.")

            val isSuccess = eventDao.postImage(
                ImageUploadRequest(
                    eventId = event.id,
                    filename = file.name,
                    image = Base64.encode(file.readBytes())
                )
            )
            if (isSuccess) {
                sv = sv.copy(updateStatus = "Uploaded image.")
                refreshEvent()
            } else {
                sv = sv.copy(updateStatus = "Failed to upload image.")
            }
        }
    }

    fun updateName(name: String) {
        event = event.copy(name = name)
    }

    fun updateCashTips(cashTips: String) {
        sv = sv.copy(cashTips = cashTips)
        event = event.copy(cashTips = cashTips.toFloatOrNull())
    }

    fun updateCardTips(cardTips: String) {
        sv = sv.copy(cardTips = cardTips)
        event = event.copy(cardTips = cardTips.toFloatOrNull())
    }

    fun updateEvent() {
        viewModelScope.launch(Dispatchers.IO) {
            val isSuccess = eventDao.update(event)
            if (isSuccess) {
                val time = Clock.System.now().toLocalDateTime().toFormatString("HH:mm")
                sv = sv.copy(updateStatus = "Success: ${time}")
                refreshEvent()
            } else {
                sv = sv.copy(updateStatus = "Failed")
            }
        }
    }

    fun clearNowPlaying() {
        viewModelScope.launch(Dispatchers.IO) {
            event = event.copy(currentRequestId = null)
            eventDao.update(event)
            if (sv.info.requests.isEmpty()) {
                requestDao.getRandomRequest(event.id)
            }
            refreshEvent()
        }
    }
}

data class EventProfileState(
    val info: EventInfo = EventInfo(),
    val imageUrl: String? = null,
    val updateStatus: String = "",
    val cashTips: String = "",
    val cardTips: String = "",
) : UiState

fun EventStatus.getButtonText() = when (this) {
    EventStatus.Pending -> "Start"
    EventStatus.Started -> "Finish"
    EventStatus.Finished -> "Resume"
}