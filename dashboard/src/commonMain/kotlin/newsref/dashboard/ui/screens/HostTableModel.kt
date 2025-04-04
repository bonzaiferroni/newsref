package newsref.dashboard.ui.screens

import androidx.lifecycle.*
import kotlinx.coroutines.*
import newsref.app.blip.core.StateModel
import newsref.dashboard.HostTableRoute
import newsref.db.model.Host
import newsref.db.services.*
import newsref.model.data.Sorting

class HostTableModel(
    val route: HostTableRoute,
    val hostService: HostService = HostService()
) : StateModel<HostTableState>(HostTableState(route.searchText ?: "")) {

    init {
         refreshItems()
    }

    private fun refreshItems() {
        viewModelScope.launch {
            val hosts = hostService.readHosts(stateNow.search)
            setState { it.copy(hosts = hosts) }
        }
    }

    fun changeSorting(sorting: Sorting) {
        setState { it.copy(sorting = sorting)}
        refreshItems()
    }

    fun changeSearch(text: String) {
        setState { it.copy(search = text) }
        refreshItems()
    }
}

data class HostTableState(
    val search: String,
    val hosts: List<Host> = emptyList(),
    val sorting: Sorting = null to null
)