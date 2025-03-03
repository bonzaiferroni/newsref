package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
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
import newsref.app.blip.core.StateModel
import newsref.app.io.ChapterStore
import newsref.app.model.ChapterPack
import newsref.app.model.toModel
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
            val eventTime = pack.chapter.averageAt - 2.days
            val now = Clock.System.now()
            val minStartTime = now - 4.days
            val startTime = when {
                eventTime > minStartTime -> minStartTime
                else -> eventTime
            }
            val endTime = startTime + 4.days
            val xTicks = generateAxisTicks(startTime)
            val config = BalloonsData(
                points = balloonPoints,
                xTicks = xTicks,
                xMax = endTime.toDaysFromNow(),
                xMin = startTime.toDaysFromNow()
            )
            setState { it.copy(pack = pack, chartConfig = config) }
        }
    }
}

data class ChapterState(
    val chartConfig: BalloonsData = BalloonsData(),
    val pack: ChapterPack? = null,
)

fun generateAxisTicks(earliest: Instant, latest: Instant = Clock.System.now()): ImmutableList<AxisTick> {
    val now = Clock.System.now()
    val span = latest - earliest
    val interval = when {
        span > 21.days -> 7.days
        span > 10.days -> 3.days
        span > 2.days -> 1.days
        else -> 6.hours
    }
    val tz = TimeZone.currentSystemDefault()
    val timeStart = (earliest + 1.days).toLocalDateTime(tz).date
        .atStartOfDayIn(tz)
    val intervalCount = (span / interval).toInt()
    val currentYear = now.toLocalDateTime(tz).year
    return (0 until intervalCount).map { i ->
        val time = timeStart + interval * i
        val localTime = time.toLocalDateTime(tz)
        val year = localTime.year.toString()
        val date = "${localTime.monthNumber}/${localTime.dayOfMonth}"
        val day = localTime.dayOfWeek.toString().take(3)
        val label = when {
            localTime.year != currentYear -> "$day,\n$date, $year"
            time < now - 7.days -> "$day,\n$date"
            localTime.hour == 0 && localTime.minute == 0 -> day
            else -> "${localTime.hour}:${localTime.minute.toString().padStart(2, '0')}"
        }
        val x = (now - time).inWholeHours / 24f
        AxisTick(-x, label)
    }.toImmutableList()
}