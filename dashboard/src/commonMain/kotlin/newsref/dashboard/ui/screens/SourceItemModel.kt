package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.dashboard.SourceItemRoute
import newsref.db.core.DistanceInfo
import newsref.db.core.SourceDistance
import newsref.db.services.ArticleService
import newsref.db.services.ContentService
import newsref.db.services.LinkService
import newsref.db.services.ScoreService
import newsref.db.services.SourceService
import newsref.db.services.VectorService
import newsref.model.data.Article
import newsref.model.data.Content
import newsref.model.data.Link
import newsref.model.data.Source
import newsref.model.data.SourceScore
import newsref.model.dto.LinkInfo
import kotlin.time.Duration.Companion.minutes

class SourceItemModel(
    route: SourceItemRoute,
    private val sourceService: SourceService = SourceService(),
    private val scoreService: ScoreService = ScoreService(),
    private val contentService: ContentService = ContentService(),
    private val articleService: ArticleService = ArticleService(),
    private val linkService: LinkService = LinkService(),
    private val vectorService: VectorService = VectorService(),
) : StateModel<SourceItemState>(
    SourceItemState(
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
        val article = articleService.readBySource(stateNow.sourceId)
        val outbound = linkService.readOutboundLinks(stateNow.sourceId)
        val inbound = linkService.readInboundLinks(stateNow.sourceId)
        val distances = vectorService.readDistances(stateNow.sourceId)
        setState { it.copy(
            source = source,
            scores = scores,
            contents = contents,
            article = article,
            outbound = outbound,
            inbound = inbound,
            distances = distances,
            nextRefresh = Clock.System.now() + 1.minutes
        ) }
    }

    fun changePage(page: String) {
        setState { it.copy(page = page) }
    }
}

data class SourceItemState(
    val sourceId: Long,
    val nextRefresh: Instant = Instant.DISTANT_PAST,
    val source: Source? = null,
    val article: Article? = null,
    val scores: List<SourceScore>? = null,
    val contents: List<Content> = emptyList(),
    val outbound: List<LinkInfo> = emptyList(),
    val inbound: List<LinkInfo> = emptyList(),
    val distances: List<DistanceInfo> = emptyList(),
    val page: String = "",
)