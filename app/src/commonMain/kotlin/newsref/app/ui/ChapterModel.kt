package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.app.ChapterRoute
import newsref.app.blip.controls.BalloonsData
import newsref.app.blip.controls.BalloonPoint
import newsref.app.blip.controls.generateAxisTicks
import newsref.app.blip.core.StateModel
import newsref.app.io.ChapterStore
import newsref.model.data.ArticleType
import newsref.model.data.Chapter
import newsref.model.data.ChapterPageLite
import newsref.model.data.PageLite
import newsref.model.utils.toDaysFromNow
import kotlin.time.Duration.Companion.days

class ChapterModel(
    private val route: ChapterRoute,
    private val chapterStore: ChapterStore = ChapterStore()
) : StateModel<ChapterState>(ChapterState()) {
    init {
        viewModelScope.launch {
            val chapter = chapterStore.readChapter(route.id)
            val pages = chapter.pages ?: error("Chapter had null pages")
            val articles = pages.filter { it.page.articleType != ArticleType.Unknown }.toImmutableList()
            val references = pages.filter { it.page.articleType == ArticleType.Unknown }.toImmutableList()
            val data = chapter.toBalloonsData()
            setState { it.copy(
                chapter = chapter,
                balloons = data,
                articles = articles,
                references = references,
            ) }
        }
    }
}

data class ChapterState(
    val balloons: BalloonsData = BalloonsData(),
    val chapter: Chapter? = null,
    val articles: ImmutableList<ChapterPageLite> = persistentListOf(),
    val references: ImmutableList<ChapterPageLite> = persistentListOf(),
)

fun Chapter.toBalloonsData(): BalloonsData {
    val pages = this.pages ?: error("Chapter had null pages")
    val dayRange = 3.0
    val eventTime = this.averageAt - (dayRange / 2).days
    val now = Clock.System.now()
    val minStartTime = now - dayRange.days
    val startTime = when {
        eventTime > minStartTime -> minStartTime
        else -> eventTime
    }
    val endTime = startTime + dayRange.days
    val balloonPoints = pages.mapNotNull {
        val page = it.page
        if (page.existedAt > endTime || page.existedAt < startTime) return@mapNotNull null
        val x = (now - page.existedAt).inWholeHours / 24f
        BalloonPoint(
            id = page.id,
            x = -x,
            y = page.score.toFloat(),
            size = page.score.toFloat(),
            text = page.headline.toString(),
            colorIndex = page.id.toInt(),
            page.imageUrl
        )
    }.toImmutableList()
    val xTicks = generateAxisTicks(startTime, endTime)
    return BalloonsData(
        points = balloonPoints,
        xTicks = xTicks,
        xMax = endTime.toDaysFromNow(),
        xMin = startTime.toDaysFromNow()
    )
}