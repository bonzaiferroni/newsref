package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.app.ChapterRoute
import newsref.app.blip.controls.BalloonPoint
import newsref.app.blip.core.StateModel
import newsref.app.io.ChapterStore
import newsref.app.model.ChapterPack
import newsref.app.model.toModel

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
            setState { it.copy(pack = pack, balloonPoints = balloonPoints) }
        }
    }
}

data class ChapterState(
    val pack: ChapterPack? = null,
    val balloonPoints: ImmutableList<BalloonPoint> = persistentListOf()
)