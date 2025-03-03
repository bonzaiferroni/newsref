package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.app.*
import newsref.app.blip.controls.BalloonsData
import newsref.app.blip.controls.BalloonPoint
import newsref.app.blip.core.*
import newsref.app.io.*
import newsref.app.model.*
import newsref.model.utils.toDaysFromNow
import kotlin.time.Duration.Companion.days

class ChapterFeedModel(
    val route: ChapterFeedRoute,
    val chapterStore: ChapterStore = ChapterStore()
) : StateModel<ChapterFeedState>(ChapterFeedState()) {
    init {
        viewModelScope.launch {
            val duration = 7.days
            val start = Clock.System.now() - duration
            val chapters = chapterStore.readChapters(start)
                .map { it.toModel() }
                .toImmutableList()

            val balloons = chapters.map { (chapter, sources) ->
                val x = (Clock.System.now() - chapter.averageAt).inWholeHours / 24f
                BalloonPoint(
                    id = chapter.id,
                    x = -x,
                    y = chapter.size.toFloat(),
                    size = chapter.score.toFloat(),
                    text = chapter.title ?: chapter.id.toString(),
                    colorIndex = chapter.id.toInt(),
                    imageUrl = sources.firstOrNull { it.imageUrl != null }?.imageUrl
                )
            }.toImmutableList()
            val chartConfig = BalloonsData(
                points = balloons,
                xTicks = generateAxisTicks(start),
                xMin = start.toDaysFromNow(),
                xMax = 0f,
            )
            setState { it.copy(chapterPacks = chapters, chartConfig = chartConfig) }
        }
    }

    fun selectId(id: Long) {
        if (id == stateNow.selectedId) return
        setState { it.copy(selectedId = id) }
    }
}

data class ChapterFeedState(
    val selectedId: Long? = null,
    val chapterPacks: ImmutableList<ChapterPack> = persistentListOf(),
    val chartConfig: BalloonsData = BalloonsData(),
)

