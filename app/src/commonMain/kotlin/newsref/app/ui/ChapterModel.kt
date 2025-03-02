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
import newsref.app.blip.controls.BalloonConfig
import newsref.app.blip.controls.BalloonPoint
import newsref.app.blip.core.StateModel
import newsref.app.io.ChapterStore
import newsref.app.model.ChapterPack
import newsref.app.model.toModel
import kotlin.text.Typography.times
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class ChapterModel(
    private val route: ChapterRoute,
    private val chapterStore: ChapterStore = ChapterStore()
) : StateModel<ChapterState>(ChapterState()) {
    init {
        viewModelScope.launch {
            val pack = chapterStore.readChapter(route.id).toModel()
            val balloonPoints = pack.sources.map {
                val x = (Clock.System.now() - it.existedAt).inWholeHours / 24f
                BalloonPoint(
                    id = it.id,
                    x = -x,
                    y = it.score.toFloat(),
                    size = it.score.toFloat(),
                    text = it.title.toString(),
                    colorIndex = pack.chapter.id.toInt(),
                    it.imageUrl
                    )
            }.toImmutableList()
            val xTicks = generateAxisTicks(Clock.System.now() - 7.days)
            val config = BalloonConfig(
                points = balloonPoints,
                xTicks = xTicks
            )
            setState { it.copy(pack = pack, chartConfig = config) }
        }
    }
}

data class ChapterState(
    val chartConfig: BalloonConfig = BalloonConfig(),
    val pack: ChapterPack? = null,
)

fun generateAxisTicks(earliest: Instant): ImmutableList<AxisTick> {
    val now = Clock.System.now()
    val span = now - earliest
    val interval = when {
        span > 2.days -> 1.days
        else -> 6.hours
    }
    val tz = TimeZone.currentSystemDefault()
    val timeStart = (earliest + 1.days).toLocalDateTime(tz).date
        .atStartOfDayIn(tz)
    val intervalCount = (span / interval).toInt()
    println(intervalCount)
    return (0 until intervalCount).map { i ->
        val time = timeStart + interval * i
        val localTime = time.toLocalDateTime(tz)
        val label = when {
            localTime.hour == 0 && localTime.minute == 0 -> localTime.dayOfWeek.toString().take(3)
            else -> "${localTime.hour}:${localTime.minute.toString().padStart(2, '0')}"
        }
        val x = (now - time).inWholeHours / 24f
        AxisTick(-x, label)
    }.toImmutableList()
}