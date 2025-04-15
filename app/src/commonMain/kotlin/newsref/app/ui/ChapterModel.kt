package newsref.app.ui

import androidx.lifecycle.viewModelScope
import io.pondlib.compose.ui.controls.BalloonPoint
import io.pondlib.compose.ui.controls.BalloonsData
import io.pondlib.compose.ui.controls.generateAxisTicks
import io.pondlib.compose.ui.core.StateModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.app.ChapterRoute
import newsref.app.io.ChapterStore
import newsref.model.data.Chapter
import newsref.model.data.ChapterPageLite
import newsref.model.data.ChapterPerson
import newsref.model.data.SourceType
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
            val articles = pages.filter { it.sourceType == SourceType.Article }.toImmutableList()
            val references = pages.filter { it.sourceType == SourceType.Reference }.toImmutableList()
            val data = chapter.toBalloonsData()
            setState { it.copy(
                chapter = chapter,
                balloons = data,
                articles = articles,
                references = references,
            ) }
        }
        viewModelScope.launch {
            val persons = chapterStore.readChapterPersons(route.id)
                .sortedByDescending { it.mentions }
                .toImmutableList()
            setState { it.copy(persons = persons) }
        }
    }
}

data class ChapterState(
    val balloons: BalloonsData = BalloonsData(),
    val chapter: Chapter? = null,
    val articles: ImmutableList<ChapterPageLite> = persistentListOf(),
    val references: ImmutableList<ChapterPageLite> = persistentListOf(),
    val persons: ImmutableList<ChapterPerson> = persistentListOf(),
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