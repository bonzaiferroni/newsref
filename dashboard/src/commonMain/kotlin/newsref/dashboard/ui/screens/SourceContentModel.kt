package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.app.core.StateModel
import newsref.dashboard.clients.SpeechClient
import newsref.db.services.ContentService
import newsref.model.data.Content
import newsref.model.data.Source
import newsref.model.dto.SourceInfo
import java.io.File

class SourceContentModel(
    private val source: Source,
    private val contents: List<Content>,
) : StateModel<SourceContentState>(SourceContentState(contents.map { it.text })) {

    fun onPlayText(text: String?) {
        setState { it.copy(playingText = text)}
    }
}

data class SourceContentState(
    val contents: List<String>,
    val playingText: String? = null,
)