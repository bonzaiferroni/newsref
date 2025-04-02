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
    private val articleStore: ArticleStore = ArticleStore(),
    private val hostStore: HostStore = HostStore(),
) : StateModel<ChapterPageState>(ChapterPageState(route.pageId)) {
    init {
        viewModelScope.launch {
            val chapter = this@ChapterPageModel.chapterStore.readChapter(route.chapterId).toModel()
            val balloons = chapter.toBalloonsData()
            setState { it.copy(chapter = chapter, balloons = balloons) }
        }
        selectSource(route.pageId)
    }

    fun selectSource(pageId: Long) {
        setState { it.copy(selectedId = pageId)}
        viewModelScope.launch {
            val chapterSource = chapterStore.readChapterSource(route.chapterId, pageId)
            setState { it.copy(chapterPage = chapterSource) }
        }
        viewModelScope.launch {
            val source = articleStore.readSource(pageId)
            val host = hostStore.readHost(source.hostId)
            setState { it.copy(article = source, host = host) }
        }
    }

    fun onChangePage(page: String) {
        setState { it.copy(page = page) }
    }
}

data class ChapterPageState(
    val selectedId: Long,
    val chapter: ChapterPack? = null,
    val chapterPage: ChapterPage? = null,
    val article: Article? = null,
    val host: Host? = null,
    val balloons: BalloonsData? = null,
    val page: String? = null,
)