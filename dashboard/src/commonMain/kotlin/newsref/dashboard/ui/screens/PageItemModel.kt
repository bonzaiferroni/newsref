package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.app.pond.core.StateModel
import newsref.dashboard.PageItemRoute
import newsref.dashboard.ui.controls.TimeChartData
import newsref.dashboard.ui.controls.createTimeSeries
import newsref.dashboard.ui.controls.toTimeChartData
import newsref.db.services.ContentService
import newsref.db.services.LinkService
import newsref.db.services.PageScoreService
import newsref.db.services.PageService
import newsref.db.services.EmbeddingService
import newsref.db.model.Article
import newsref.db.model.Content
import newsref.db.model.Page
import newsref.db.services.ArticleService
import newsref.model.data.LinkInfo
import kotlin.time.Duration.Companion.minutes

class PageItemModel(
    route: PageItemRoute,
    private val pageService: PageService = PageService(),
    private val pageScoreService: PageScoreService = PageScoreService(),
    private val contentService: ContentService = ContentService(),
    private val articleService: ArticleService = ArticleService(),
    private val linkService: LinkService = LinkService(),
    private val embeddingService: EmbeddingService = EmbeddingService(),
) : StateModel<PageItemState>(
    PageItemState(
        pageId = route.pageId,
        tab = route.tab
    )
) {
    init {
        viewModelScope.launch {
            refreshItem()
            delay(stateNow.nextRefresh - Clock.System.now())
        }
    }

    private suspend fun refreshItem() {
        val page = pageService.readPageById(stateNow.pageId)
        val scores = pageScoreService.readScores(stateNow.pageId)
        val contents = contentService.readSourceContent(stateNow.pageId)
        val outbound = linkService.readOutboundLinks(stateNow.pageId)
        val inbound = linkService.readInboundLinks(stateNow.pageId)
        setState {
            it.copy(
                page = page,
                chartData = scores.toTimeChartData(
                    getTime = { it.scoredAt },
                    buckets = null,
                    createTimeSeries("Score") { it.score.toFloat() }
                ),
                contents = contents,
                outbound = outbound,
                inbound = inbound,
                nextRefresh = Clock.System.now() + 1.minutes
            )
        }
    }

    fun changePage(page: String) {
        setState { it.copy(tab = page) }
    }
}

data class PageItemState(
    val pageId: Long,
    val nextRefresh: Instant = Instant.DISTANT_PAST,
    val page: Page? = null,
    val article: Article? = null,
    val chartData: TimeChartData? = null,
    val contents: List<Content> = emptyList(),
    val outbound: List<LinkInfo> = emptyList(),
    val inbound: List<LinkInfo> = emptyList(),
    val tab: String = "",
)