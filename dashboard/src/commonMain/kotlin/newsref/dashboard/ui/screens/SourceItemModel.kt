package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.app.blip.core.StateModel
import newsref.dashboard.SourceItemRoute
import newsref.dashboard.ui.controls.TimeChartData
import newsref.dashboard.ui.controls.createTimeSeries
import newsref.dashboard.ui.controls.toTimeChartData
import newsref.db.core.DistanceInfo
import newsref.db.services.ArticleService
import newsref.db.services.ContentService
import newsref.db.services.LinkService
import newsref.db.services.SourceScoreService
import newsref.db.services.SourceService
import newsref.db.services.SourceVectorService
import newsref.model.data.Article
import newsref.model.data.Content
import newsref.model.data.Source
import newsref.model.dto.LinkInfo
import kotlin.time.Duration.Companion.minutes

class SourceItemModel(
    route: SourceItemRoute,
    private val sourceService: SourceService = SourceService(),
    private val sourceScoreService: SourceScoreService = SourceScoreService(),
    private val contentService: ContentService = ContentService(),
    private val articleService: ArticleService = ArticleService(),
    private val linkService: LinkService = LinkService(),
    private val sourceVectorService: SourceVectorService = SourceVectorService(),
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
        val scores = sourceScoreService.readScores(stateNow.sourceId)
        val contents = contentService.getSourceContent(stateNow.sourceId)
        val article = articleService.readBySource(stateNow.sourceId)
        val outbound = linkService.readOutboundLinks(stateNow.sourceId)
        val inbound = linkService.readInboundLinks(stateNow.sourceId)
        val distances = sourceVectorService.readDistances(stateNow.sourceId)
        setState {
            it.copy(
                source = source,
                chartData = scores.toTimeChartData(
                    getTime = { it.scoredAt },
                    buckets = null,
                    createTimeSeries("Score") { it.score.toFloat() }
                ),
                contents = contents,
                article = article,
                outbound = outbound,
                inbound = inbound,
                distances = distances,
                nextRefresh = Clock.System.now() + 1.minutes
            )
        }
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
    val chartData: TimeChartData? = null,
    val contents: List<Content> = emptyList(),
    val outbound: List<LinkInfo> = emptyList(),
    val inbound: List<LinkInfo> = emptyList(),
    val distances: List<DistanceInfo> = emptyList(),
    val page: String = "",
)