package newsref.app.ui

import androidx.lifecycle.viewModelScope
import pondui.ui.controls.BalloonsData
import kotlinx.coroutines.launch
import newsref.app.*
import pondui.ui.core.*
import newsref.app.io.*
import newsref.model.data.Chapter
import newsref.model.data.ChapterPage
import newsref.model.data.Host
import newsref.model.data.Page

class ChapterPageModel(
    private val route: ChapterPageRoute,
    private val chapterStore: ChapterStore = ChapterStore(),
    private val pageStore: PageStore = PageStore(),
    private val hostStore: HostStore = HostStore(),
) : StateModel<ChapterPageState>(ChapterPageState(route.pageId)) {
    init {
        viewModelScope.launch {
            val chapterPack = this@ChapterPageModel.chapterStore.readChapter(route.chapterId)
            val balloons = chapterPack.toBalloonsData()
            setState { it.copy(chapterPack = chapterPack, balloons = balloons) }
        }
        selectPage(route.pageId)
    }

    fun selectPage(pageId: Long) {
        setState { it.copy(selectedId = pageId)}
        viewModelScope.launch {
            val chapterSource = chapterStore.readChapterPage(route.chapterId, pageId)
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
    val chapterPack: Chapter? = null,
    val chapterPage: ChapterPage? = null,
    val page: Page? = null,
    val host: Host? = null,
    val balloons: BalloonsData? = null,
    val tab: String? = null,
)