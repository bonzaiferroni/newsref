package newsref.dashboard.ui.screens

import newsref.app.pond.core.StateModel
import newsref.db.model.Content
import newsref.db.model.Page

class PageContentModel(
    private val page: Page,
    private val contents: List<Content>,
) : StateModel<PageContentState>(PageContentState(contents.map { it.text })) {

    fun onPlayText(text: String?) {
        setState { it.copy(playingText = text)}
    }
}

data class PageContentState(
    val contents: List<String>,
    val playingText: String? = null,
)