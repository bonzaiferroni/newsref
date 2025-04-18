package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import newsref.app.pond.core.StateModel
import newsref.dashboard.ui.controls.CloudPoint
import newsref.dashboard.utils.emptyImmutableList
import newsref.db.model.Chapter
import newsref.db.services.*
import newsref.model.data.DataSort
import newsref.model.data.SortDirection
import newsref.model.data.Sorting
import newsref.model.data.sortedByDirection
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class ChapterTableModel(
    private val chapterComposerService: ChapterService = ChapterService()
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
        val chapters = chapterCache.filter { it.averageAt > Clock.System.now() - stateNow.since }
            .sort(stateNow.sorting)
        val cloudPoints = chapters.map {
            val x = (Clock.System.now() - it.averageAt).inWholeHours / 24f
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
        DataSort.Time -> this.sortedByDirection(sorting.second) { it.averageAt }
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