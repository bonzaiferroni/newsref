package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.dashboard.ScreenRoute
import newsref.dashboard.SourceItemRoute
import newsref.db.services.ScoreService
import newsref.db.services.SourceService
import newsref.model.data.SourceScore
import newsref.model.dto.SourceInfo

class SourceItemModel(
    route: SourceItemRoute,
    private val sourceService: SourceService = SourceService(),
    private val scoreService: ScoreService = ScoreService()
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
        val source = sourceService.getSourceInfo(stateNow.sourceId)
        val scores = scoreService.readScores(stateNow.sourceId)
        setState { it.copy(source = source, scores = scores) }
    }

    fun changePage(page: String) {
        setState { it.copy(page = page) }
    }
}

data class SourceRowState(
    val sourceId: Long,
    val nextRefresh: Instant = Instant.DISTANT_PAST,
    val source: SourceInfo? = null,
    val scores: List<SourceScore>? = null,
    val page: String = "",
)