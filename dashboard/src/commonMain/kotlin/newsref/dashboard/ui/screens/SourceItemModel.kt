package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.dashboard.SourceItemRoute
import newsref.db.services.ContentService
import newsref.db.services.ScoreService
import newsref.db.services.SourceService
import newsref.model.data.Content
import newsref.model.data.Source
import newsref.model.data.SourceScore
import newsref.model.dto.SourceInfo

class SourceItemModel(
    route: SourceItemRoute,
    private val sourceService: SourceService = SourceService(),
    private val scoreService: ScoreService = ScoreService(),
    private val contentService: ContentService = ContentService(),
) : StateModel<SourceRowState>(
    SourceRowState(
        sourceId = route.sourceId,
        page = route.pageName
    )
) {
    init {
        viewModelScope.launch {
            refreshItem()
            delay(stateNow.nextRefresh - Clock.System.now())
        }
    }

    private suspend fun refreshItem() {
        val source = sourceService.getSource(stateNow.sourceId)
        val scores = scoreService.readScores(stateNow.sourceId)
        val contents = contentService.getSourceContent(stateNow.sourceId)
        setState { it.copy(source = source, scores = scores, contents = contents) }
    }

    fun changePage(page: String) {
        setState { it.copy(page = page) }
    }
}

data class SourceRowState(
    val sourceId: Long,
    val nextRefresh: Instant = Instant.DISTANT_PAST,
    val source: Source? = null,
    val scores: List<SourceScore>? = null,
    val contents: List<Content>? = null,
    val page: String = "",
)