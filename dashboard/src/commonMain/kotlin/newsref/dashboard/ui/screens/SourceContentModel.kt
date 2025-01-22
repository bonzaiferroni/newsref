package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.db.services.ContentService
import newsref.model.data.Content
import newsref.model.data.Source
import newsref.model.dto.SourceInfo

class SourceContentModel(
    source: Source,
    private val contentService: ContentService = ContentService(),
) : StateModel<SourceContentState>(SourceContentState(source)) {

    init {
        viewModelScope.launch {
            val contents = contentService.getSourceContent(source.id)
            setState { it.copy(contents = contents) }
        }
    }
}

data class SourceContentState(
    val source: Source,
    val contents: List<Content> = emptyList()
)