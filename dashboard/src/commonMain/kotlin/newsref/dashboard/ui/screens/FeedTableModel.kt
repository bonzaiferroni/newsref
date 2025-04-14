package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import newsref.app.pond.core.StateModel
import newsref.dashboard.FeedTableRoute
import newsref.dashboard.utils.*
import newsref.db.core.toUrl
import newsref.db.core.toUrlOrNull
import newsref.db.model.Feed
import newsref.db.services.*
import newsref.model.data.DataSort
import newsref.model.data.Sorting
import newsref.model.data.sortedByDirection
import kotlin.time.Duration.Companion.minutes

class FeedTableModel(
    private val route: FeedTableRoute,
    private val feedService: FeedService = FeedService(),
    private val leadService: LeadService = LeadService(),
) : StateModel<FeedTableState>(FeedTableState(page = route.page)) {

    var sorting: Sorting = null to null

    init {
        viewModelScope.launch {
            while (true) {
                refresh()
                delay(1.minutes)
            }
        }
    }

    suspend fun refresh() {
        val items = feedService.readAll().sort(sorting).toImmutableList()
        val leadCounts = leadService.getAllFeedLeads().groupBy { it.feedId }
            .filterKeys { it != null }
            .mapKeys { it.key!! }
            .mapValues { it.value.size }
            .toImmutableMap()
        setState {
            it.copy(
                items = items,
                leadCounts = leadCounts
            )
        }
    }

    fun changeHref(value: String) {
        val updatedUrl = value.toUrlOrNull()
        setState {
            it.copy(
                newItem = it.newItem.copy(url = updatedUrl ?: it.newItem.url),
                newHref = value
            )
        }
    }

    fun changeNewItem(item: Feed) { setState { it.copy(newItem = item) } }

    fun changeSorting(sorting: Sorting) {
        this.sorting = sorting
        setState { it.copy(items = stateNow.items.sort(sorting))}
    }

    fun addNewItem() {
        if (!stateNow.canAddItem) return
        viewModelScope.launch {
            feedService.create(stateNow.newItem)
            setState { it.copy(newItem = emptyFeed) }
            refresh()
        }
    }

    private fun List<Feed>.sort(sorting: Sorting) = when (sorting.first) {
        DataSort.Id -> this.sortedByDirection(sorting.second) { it.id }
        DataSort.Time -> this.sortedByDirection(sorting.second) { it.checkAt }
        DataSort.Name -> this.sortedByDirection(sorting.second) { it.url.core }
        DataSort.Score -> this.sortedByDirection(sorting.second) { stateNow.leadCounts[it.id] ?: 0 }
        null -> this.sortedByDirection(sorting.second) { it.checkAt }
    }.toImmutableList()

    fun changePage(page: String) { setState { it.copy(page = page) } }

    fun changeShowDisabled(value: Boolean) { setState { it.copy(showDisabled = value) } }

    fun changeShowExternal(value: Boolean) { setState { it.copy(showExternal = value) } }

    fun changeShowTrackPosition(value: Boolean) { setState { it.copy(showTrackPosition = value) } }

    fun changeShowDebug(value: Boolean) { setState { it.copy(showDebug = value) } }

    fun changeShowSelector(value: Boolean) { setState { it.copy(showSelector = value) } }
}

data class FeedTableState(
    val newItem: Feed = emptyFeed,
    val newHref: String = emptyFeed.url.href,
    val page: String? = null,
    val showDisabled: Boolean = false,
    val showExternal: Boolean = true,
    val showTrackPosition: Boolean = true,
    val showDebug: Boolean = true,
    val showSelector: Boolean = true,
    val items: ImmutableList<Feed> = emptyImmutableList(),
    val leadCounts: ImmutableMap<Int, Int> = emptyImmutableMap(),
) {
    val canAddItem get() = newHref.toUrlOrNull() != null && !newItem.selector.isNullOrBlank()
}

private val emptyFeed = Feed(
    id = 0,
    url = "https://example.com/".toUrl(),
    selector = "",
    external = false,
    trackPosition = false,
    debug = true,
    createdAt = Clock.System.now(),
    checkAt = Clock.System.now(),
)