package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import newsref.app.ChapterRoute
import newsref.app.blip.controls.AxisTick
import newsref.app.blip.controls.BalloonsData
import newsref.app.blip.controls.BalloonPoint
import newsref.app.blip.controls.generateAxisTicks
import newsref.app.blip.core.StateModel
import newsref.app.io.ChapterStore
import newsref.app.model.ChapterPack
import newsref.app.model.SourceBit
import newsref.app.model.toModel
import newsref.model.core.PageType
import newsref.model.utils.toDaysFromNow
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class ChapterModel(
    private val route: ChapterRoute,
    private val chapterStore: ChapterStore = ChapterStore()
) : StateModel<ChapterState>(ChapterState()) {
    init {
        viewModelScope.launch {
            val pack = chapterStore.readChapter(route.id).toModel()
            val articles = pack.sources.filter { it.pageType == PageType.NEWS_ARTICLE }.toImmutableList()
            val references = pack.sources.filter { it.pageType != PageType.NEWS_ARTICLE }.toImmutableList()
            val data = pack.toBalloonsData()
            setState { it.copy(
                pack = pack,
                balloons = data,
                articles = articles,
                references = references,
            ) }
        }
    }

    fun changeTab(tab: String) {
        setState { it.copy(tab = tab) }
    }
}

data class ChapterState(
    val balloons: BalloonsData = BalloonsData(),
    val pack: ChapterPack? = null,
    val articles: ImmutableList<SourceBit> = persistentListOf(),
    val references: ImmutableList<SourceBit> = persistentListOf(),
    val tab: String? = null,
)

fun ChapterPack.toBalloonsData(): BalloonsData {
    val balloonPoints = this.sources.map {
        val x = (Clock.System.now() - it.existedAt).inWholeHours / 24f
        BalloonPoint(
            id = it.id,
            x = -x,
            y = it.score.toFloat(),
            size = it.score.toFloat(),
            text = it.title.toString(),
            colorIndex = this.chapter.id.toInt(),
            it.imageUrl
        )
    }.toImmutableList()
    val dayRange = 3.0
    val eventTime = this.chapter.averageAt - (dayRange / 2).days
    val now = Clock.System.now()
    val minStartTime = now - dayRange.days
    val startTime = when {
        eventTime > minStartTime -> minStartTime
        else -> eventTime
    }
    val endTime = startTime + dayRange.days
    val xTicks = generateAxisTicks(startTime)
    return BalloonsData(
        points = balloonPoints,
        xTicks = xTicks,
        xMax = endTime.toDaysFromNow(),
        xMin = startTime.toDaysFromNow()
    )
}