package newsref.dashboard.utils

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.dashboard.clients.SpeechClient
import newsref.app.core.StateModel
import java.io.File

class SpeechPlayerModel(
    private val contents: List<String>,
    autoPlay: Boolean,
    private val speechClient: SpeechClient = SpeechClient(),
) : StateModel<AudioPlayerState>(AudioPlayerState()) {

    private val files = MutableList<String?>(contents.size) { null }
    private var currentIndex = 0

    init {
        if (autoPlay) toggleIsPlaying()
    }

    private fun loadFiles() {
        viewModelScope.launch {
            loadIndex(0)
        }
    }

    private suspend fun loadIndex(index: Int) {
        if (index < 0 || index >= contents.size) return
        if (files[index] != null) return

        val path = "../cache/wav/$index.wav"
        val file = File(path)
        file.parentFile?.mkdirs()
        val bytes = speechClient.textToSpeech(contents[index])
        file.writeBytes(bytes)
        files[index] = path
        if (index == currentIndex && stateNow.url == null) {
            playIndex(currentIndex)
        }
    }

    fun toggleIsPlaying() {
        val isPlaying = !stateNow.isPlaying
        setState { it.copy(isPlaying = isPlaying) }
        if (isPlaying && currentIndex == 0) {
            loadFiles()
        }
    }

    fun playNext() {
        playIndex(++currentIndex)
    }

    fun playPrevious() {
        playIndex(--currentIndex)
    }

    fun playIndex(index: Int) {
        if (index < 0) return
        currentIndex = index
        val url = if (index < files.size) files[index] else null
        val text = if (index < contents.size) contents[index] else null
        setState { it.copy(url = url, text = text, isFinished = index == contents.size) }
        viewModelScope.launch {
            loadIndex(currentIndex + 1)
        }
    }
}

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val url: String? = null,
    val files: List<String>? = null,
    val text: String? = null,
    val isFinished: Boolean = false,
)