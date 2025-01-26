package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.dashboard.clients.SpeechClient
import newsref.db.services.ContentService
import newsref.model.data.Content
import newsref.model.data.Source
import newsref.model.dto.SourceInfo
import java.io.File

class SourceContentModel(
    private val source: Source,
    private val contents: List<Content>,
    private val speechClient: SpeechClient = SpeechClient(),
) : StateModel<SourceContentState>(SourceContentState()) {

    fun speak() {
        setState { it.copy(speak = true)}
        viewModelScope.launch {
            val files = contents.mapIndexed { index, content ->
                val path = "../cache/wav/${source.id}_$index.wav"
                val file = File(path)
                if (!file.exists()) {
                    val bytes = speechClient.textToSpeech(content.text)
                    file.parentFile?.mkdirs()
                    file.writeBytes(bytes)
                }
                println(path)
                path
            }
            setState { it.copy(files= files)}
        }
    }
}

data class SourceContentState(
    val speak: Boolean = false,
    val files: List<String>? = null
)