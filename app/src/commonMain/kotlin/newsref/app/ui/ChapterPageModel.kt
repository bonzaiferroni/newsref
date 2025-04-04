package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.app.*
import newsref.app.blip.controls.BalloonsData
import newsref.app.blip.core.*
import newsref.app.io.*
import newsref.app.model.*

class ChapterPageModel(
    private val route: ChapterPageRoute,
    private val chapterStore: ChapterStore = ChapterStore(),
    private val pageStore: PageStore = PageStore(),
    private val hostStore: HostStore = HostStore(),
) : StateModel<ChapterPageState>(ChapterPageState(route.pageId)) {
    init {
        viewModelScope.launch {
            val chapter = this@ChapterPageModel.chapterStore.readChapter(route.chapterId).toModel()
            val balloons = chapter.toBalloonsData()
            setState { it.copy(chapter = chapter, balloons = balloons) }
        }
        selectPage(route.pageId)
    }

    fun selectPage(pageId: Long) {
        setState { it.copy(selectedId = pageId)}
        viewModelScope.launch {
            val chapterSource = chapterStore.readChapterSource(route.chapterId, pageId)
            setState { it.copy(chapterPage = chapterSource) }
        }
        viewModelScope.launch {
            val page = pageStore.readPage(pageId)
            val host = hostStore.readHost(page.hostId)
            setState { it.copy(page = page, host = host) }
        }
    }

    fun onChangeTab(page: String) {
        setState { it.copy(tab = page) }
    }
}

data class ChapterPageState(
    val selectedId: Long,
    val chapter: ChapterPack? = null,
    val chapterPage: ChapterPage? = null,
    val page: Page? = null,
    val host: Host? = null,
    val balloons: BalloonsData? = null,
    val tab: String? = null,
)