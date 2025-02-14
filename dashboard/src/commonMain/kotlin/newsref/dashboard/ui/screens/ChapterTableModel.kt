package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.app.blip.core.StateModel
import newsref.dashboard.ui.controls.CloudPoint
import newsref.dashboard.utils.emptyImmutableList
import newsref.db.services.*
import newsref.model.core.DataSort
import newsref.model.core.SortDirection
import newsref.model.core.Sorting
import newsref.model.core.sortedByDirection
import newsref.model.data.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class ChapterTableModel(
    private val chapterComposerService: ChapterComposerService = ChapterComposerService()
) : StateModel<ChapterTableState>(ChapterTableState()) {

    init {
        viewModelScope.launch {
            refreshData()
        }
    }

    var chapterCache: List<Chapter> = emptyList()

    fun changeSort(sorting: Sorting) {
        setState { it.copy(sorting = sorting) }
        sortAndFilterItems()
    }

    fun changeSince(since: Duration) {
        setState { it.copy(since = since) }
        sortAndFilterItems()
    }

    fun selectId(id: Long) {
        setState { it.copy(selectedId = id) }
    }

    private suspend fun refreshData() {
        chapterCache = chapterComposerService.readChapters()
        sortAndFilterItems()
    }

    private fun sortAndFilterItems() {
        val chapters = chapterCache.filter { it.happenedAt > Clock.System.now() - stateNow.since }
            .sort(stateNow.sorting)
        val cloudPoints = chapters.map {
            val x = (Clock.System.now() - it.happenedAt).inWholeHours / 24f
            CloudPoint(
                id = it.id,
                x = -x,
                y = it.size.toFloat(),
                size = it.score.toFloat(),
                text = it.title ?: it.id.toString()
            )
        }
            .sortedBy { it.size }
            .toImmutableList()
        setState { it.copy(chapters = chapters, cloudPoints = cloudPoints) }
    }

    private fun List<Chapter>.sort(sorting: Sorting) = when (sorting.first) {
        DataSort.Id -> this.sortedByDirection(sorting.second) { it.id }
        DataSort.Time -> this.sortedByDirection(sorting.second) { it.happenedAt }
        DataSort.Name -> error("no name sorting provided")
        DataSort.Score, null -> this.sortedByDirection(sorting.second) { it.score }
    }.toImmutableList()
}

data class ChapterTableState(
    val chapters: ImmutableList<Chapter> = emptyImmutableList(),
    val since: Duration = 7.days,
    val sorting: Sorting = Sorting(DataSort.Score, SortDirection.Descending),
    val cloudPoints: ImmutableList<CloudPoint> = emptyImmutableList(),
    val selectedId: Long? = null,
)