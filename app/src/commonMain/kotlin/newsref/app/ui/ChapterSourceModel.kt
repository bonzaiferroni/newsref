package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.app.*
import newsref.app.blip.controls.BalloonsData
import newsref.app.blip.core.*
import newsref.app.io.ChapterStore
import newsref.app.model.*
import newsref.model.dto.ChapterSourceDto

class ChapterSourceModel(
    private val route: ChapterSourceRoute,
    private val store: ChapterStore = ChapterStore()
) : StateModel<ChapterSourceState>(ChapterSourceState(route.pageId)) {
    init {
        viewModelScope.launch {
            val chapter = store.readChapter(route.chapterId).toModel()
            val balloons = chapter.toBalloonsData()
            setState { it.copy(chapter = chapter, balloons = balloons) }
        }
        selectSource(route.pageId)
    }

    fun selectSource(pageId: Long) {
        viewModelScope.launch {
            val source = store.readChapterSource(route.chapterId, pageId)
            setState { it.copy(source = source) }
        }
        setState { it.copy(selectedId = pageId)}
    }
}

data class ChapterSourceState(
    val selectedId: Long,
    val chapter: ChapterPack? = null,
    val source: ChapterSource? = null,
    val balloons: BalloonsData? = null
)