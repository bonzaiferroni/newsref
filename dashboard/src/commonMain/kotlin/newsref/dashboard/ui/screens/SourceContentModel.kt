package newsref.dashboard.ui.screens

import newsref.app.blip.core.StateModel
import newsref.db.model.Content
import newsref.db.model.Source

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