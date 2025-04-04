package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import newsref.app.blip.core.StateModel
import newsref.dashboard.ChapterItemRoute
import newsref.dashboard.utils.emptyImmutableList
import newsref.db.model.Chapter
import newsref.db.model.ChapterPageInfo
import newsref.db.model.NewsSourceType
import newsref.db.services.*
import newsref.model.data.DataSort
import newsref.model.data.Sorting
import newsref.model.data.sortedByDirection

class ChapterItemModel(
    route: ChapterItemRoute,
    private val chapterService: ChapterService = ChapterService(),
    private val chapterComposerService: ChapterComposerService = ChapterComposerService(),
    private val chapterLinkerService: ChapterLinkerService = ChapterLinkerService(),
    private val chapterWatcherService: ChapterWatcherService = ChapterWatcherService(),
) : StateModel<ChapterItemState>(ChapterItemState(route.chapterId)) {

    init {
        viewModelScope.launch {
            refreshModel()
        }
    }

    suspend fun refreshModel() {
        val chapter = chapterService.readChapter(stateNow.chapterId)
        val chapterSources = chapterWatcherService.readChapterSourceInfos(stateNow.chapterId)
        val primarySources = chapterSources.filter { it.chapterPage.type == NewsSourceType.Primary }
        val secondarySources = chapterSources.filter { it.chapterPage.type == NewsSourceType.Secondary }
        val children = chapterLinkerService.readChildren(stateNow.chapterId)
            .sortedBy { it.averageAt }
            .toImmutableList()
        val nextChildId = children.firstOrNull()?.id
        setState { it.copy(
            chapter = chapter,
            primaries = primarySources,
            secondaries = secondarySources,
            children = children,
            nextChildId = nextChildId
        ) }
    }

    fun changePage(page: String) {
        setState { it.copy(page = page) }
    }

    fun sortSources(sorting: Sorting) {
        setState { it.copy(
            primaries = stateNow.primaries.sort(sorting),
            secondaries = stateNow.secondaries.sort(sorting)
        ) }
    }

    private fun List<ChapterPageInfo>.sort(sorting: Sorting) = when (sorting.first) {
        DataSort.Id -> this.sortedByDirection(sorting.second) { it.chapterPage.id }
        DataSort.Time -> this.sortedByDirection(sorting.second) { it.page.seenAt }
        DataSort.Name -> error("no name sorting provided")
        DataSort.Score, null -> this.sortedByDirection(sorting.second) { it.chapterPage.distance ?: 0f }
    }.toImmutableList()
}

data class ChapterItemState(
    val chapterId: Long,
    val chapter: Chapter? = null,
    val page: String? = null,
    val primaries: List<ChapterPageInfo> = emptyList(),
    val secondaries: List<ChapterPageInfo> = emptyList(),
    val children: ImmutableList<Chapter> = emptyImmutableList(),
    val nextChildId : Long? = null
)