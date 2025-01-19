package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.db.services.ContentService
import newsref.model.data.Content
import newsref.model.dto.SourceInfo

class SourceContentModel(
    source: SourceInfo,
    private val contentService: ContentService = ContentService(),
) : ScreenModel<SourceContentState>(SourceContentState(source)) {

    init {
        viewModelScope.launch {
            val contents = contentService.getSourceContent(source.sourceId)
            setState { it.copy(contents = contents) }
        }
    }
}

data class SourceContentState(
    val source: SourceInfo,
    val contents: List<Content> = emptyList()
)