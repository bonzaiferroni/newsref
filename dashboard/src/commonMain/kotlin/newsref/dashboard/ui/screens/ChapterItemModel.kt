package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import newsref.app.blip.core.StateModel
import newsref.dashboard.ChapterItemRoute
import newsref.dashboard.utils.emptyImmutableList
import newsref.db.services.*
import newsref.model.core.DataSort
import newsref.model.core.Sorting
import newsref.model.core.sortedByDirection
import newsref.model.data.*

class ChapterItemModel(
    route: ChapterItemRoute,
    private val chapterComposerService: ChapterComposerService = ChapterComposerService()
) : StateModel<ChapterItemState>(ChapterItemState(route.chapterId)) {

    init {
        viewModelScope.launch {
            refreshModel()
        }
    }

    suspend fun refreshModel() {
        val chapter = chapterComposerService.readChapter(stateNow.chapterId)
        val chapterSources = chapterComposerService.readChapterSourceInfos(stateNow.chapterId)
        val primarySources = chapterSources.filter { it.chapterSource.type == ChapterSourceType.Primary }
        val secondarySources = chapterSources.filter { it.chapterSource.type == ChapterSourceType.Secondary }
        val children = chapterComposerService.readChildren(stateNow.chapterId)
            .sortedBy { it.happenedAt }
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

    private fun List<ChapterSourceInfo>.sort(sorting: Sorting) = when (sorting.first) {
        DataSort.Id -> this.sortedByDirection(sorting.second) { it.chapterSource.id }
        DataSort.Time -> this.sortedByDirection(sorting.second) { it.source.seenAt }
        DataSort.Name -> error("no name sorting provided")
        DataSort.Score, null -> this.sortedByDirection(sorting.second) { it.chapterSource.distance ?: 0f }
    }.toImmutableList()
}

data class ChapterItemState(
    val chapterId: Long,
    val chapter: Chapter? = null,
    val page: String? = null,
    val primaries: List<ChapterSourceInfo> = emptyList(),
    val secondaries: List<ChapterSourceInfo> = emptyList(),
    val children: ImmutableList<Chapter> = emptyImmutableList(),
    val nextChildId : Long? = null
)