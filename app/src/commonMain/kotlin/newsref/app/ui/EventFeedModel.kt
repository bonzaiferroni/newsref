package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.app.*
import newsref.app.blip.controls.BalloonPoint
import newsref.app.blip.core.*
import newsref.app.io.*
import newsref.app.model.*

class EventFeedModel(
    val route: EvemtFeedRoute,
    val chapterStore: ChapterStore = ChapterStore()
) : StateModel<ChapterFeedState>(ChapterFeedState()) {
    init {
        viewModelScope.launch {
            val chapters = chapterStore.readChapters()
                .map { it.toModel() }
                .toImmutableList()

            val balloons = chapters.map { (chapter, sources) ->
                val x = (Clock.System.now() - chapter.happenedAt).inWholeHours / 24f
                BalloonPoint(
                    id = chapter.id,
                    x = -x,
                    y = chapter.size.toFloat(),
                    size = chapter.score.toFloat(),
                    text = chapter.title ?: chapter.id.toString()
                )
            }.toImmutableList()
            setState { it.copy(chapterPacks = chapters, balloonPoints = balloons) }
        }
    }
}

data class ChapterFeedState(
    val chapterPacks: ImmutableList<ChapterPack> = persistentListOf(),
    val balloonPoints: ImmutableList<BalloonPoint> = persistentListOf(),
)

