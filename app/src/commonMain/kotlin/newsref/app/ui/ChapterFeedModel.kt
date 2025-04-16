package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.app.*
import pondui.ui.controls.*
import pondui.ui.core.*
import newsref.app.io.*
import newsref.model.data.Chapter
import newsref.model.utils.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class ChapterFeedModel(
    val route: ChapterFeedRoute,
    val chapterStore: ChapterStore = ChapterStore()
) : StateModel<ChapterFeedState>(ChapterFeedState(feedSpan = FeedSpan.fromOrdinal(route.feedSpan))) {
    init {
        viewModelScope.launch {
            while (true) {
                changeSpan(stateNow.feedSpan)
                delay(5.minutes)
            }
        }
    }

    fun selectId(id: Long) {
        if (id == stateNow.selectedId) return
        setState { it.copy(selectedId = id) }
    }

    fun changeSpan(span: FeedSpan) {
        if (span == stateNow.feedSpan && stateNow.chapters.isNotEmpty()) return
        setState { it.copy(feedSpan = span) }
        viewModelScope.launch {
            val start = Clock.System.now() - stateNow.feedSpan.duration
            val chapters = chapterStore.readChapters(start)
                .sortedByDescending { it.size }
                .toImmutableList()

            val balloons = chapters.map { chapter ->
                val x = (Clock.System.now() - chapter.averageAt).inWholeHours / 24f
                BalloonPoint(
                    id = chapter.id,
                    x = -x,
                    y = chapter.score.toFloat(),
                    size = chapter.size.toFloat(),
                    text = chapter.title ?: chapter.id.toString(),
                    colorIndex = chapter.id.toInt(),
                    imageUrl = chapter.imageUrl
                )
            }.toImmutableList()
            val chartConfig = BalloonsData(
                points = balloons,
                xTicks = generateAxisTicks(start),
                xMin = start.toDaysFromNow(),
                xMax = 0f,
            )
            setState { it.copy(chapters = chapters, chartConfig = chartConfig) }
        }
    }
}

data class ChapterFeedState(
    val selectedId: Long? = null,
    val chapters: ImmutableList<Chapter> = persistentListOf(),
    val chartConfig: BalloonsData = BalloonsData(),
    val feedSpan: FeedSpan = FeedSpan.Week
)

val feedSpans: ImmutableList<Pair<FeedSpan, String>> = FeedSpan.entries.map {
    it to it.label
}.toImmutableList()

enum class FeedSpan(val duration: Duration, val label: String) {
    Day(1.days, "Day"),
    Week(7.days, "Week"),
    Month(30.days, "Month"),
    Year(365.days, "Year"),
    All(Duration.INFINITE, "All");

    companion object {
        fun fromOrdinal(ordinal: Int) = entries.getOrNull(ordinal) ?: Week
    }
}