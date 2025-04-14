package newsref.dashboard.ui.screens

import androidx.lifecycle.*
import kotlinx.collections.immutable.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import newsref.app.pond.core.StateModel
import newsref.dashboard.HostItemRoute
import newsref.dashboard.ui.controls.CloudPoint
import newsref.dashboard.utils.*
import newsref.db.model.Host
import newsref.db.services.*
import newsref.model.data.PageInfo
import newsref.model.data.Sorting
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class HostItemModel(
    private val route: HostItemRoute,
    private val hostService: HostService = HostService()
) : StateModel<HostItemState>(HostItemState(route.hostId, route.tab)) {
    init {
        refreshItem()
    }

    private fun refreshItem() {
        viewModelScope.launch {
            val host = hostService.readHost(route.hostId)
            setState { it.copy(host = host)}
        }
    }

    private fun refreshSources() {
        viewModelScope.launch {
            val (sort, direction) = stateNow.sorting
            val sources = hostService.readHostSources(
                hostId = route.hostId,
                interval = stateNow.since,
                searchText = stateNow.searchText,
                sort = sort,
                direction = direction
            ).toImmutableList()
            val clouds = sources.map {
                CloudPoint(
                    it.pageId,
                    -(Clock.System.now() - it.existedAt).inWholeHours / 24.0f,
                    it.score.toFloat(),
                    it.score.toFloat(),
                    it.pageTitle ?: it.pageId.toString()
                )
            }.toImmutableList()
            setState { it.copy(sources = sources, clouds = clouds) }
        }
    }

    fun changePage(page: String) {
        setState { it.copy(page = page) }
    }

    fun changeSorting(sorting: Sorting) {
        setState { it.copy(sorting = sorting)}
        refreshSources()
    }

    fun onSearch(searchText: String) {
        setState { it.copy(searchText = searchText) }
        refreshSources()
    }

    fun changeSince(since: Duration) {
        setState { it.copy(since = since) }
        refreshSources()
    }

    fun changeSelected(selectedId: Long) {
        setState { it.copy(selectedId = selectedId) }
    }
}

data class HostItemState(
    val hostId: Int,
    val page: String? = null,
    val host: Host? = null,
    val sources: ImmutableList<PageInfo> = emptyImmutableList(),
    val sorting: Sorting = null to null,
    val searchText: String = "",
    val since: Duration = 7.days,
    val clouds: ImmutableList<CloudPoint> = emptyImmutableList(),
    val selectedId: Long? = null
)