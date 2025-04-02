
package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.app.blip.core.StateModel
import newsref.db.services.*
import newsref.model.dto.*

class PageTableModel(
    private val pageService: PageService = PageService()
) : StateModel<PageTableState>(PageTableState()) {

    private var firstVisibleIndex = 0

    init {
        viewModelScope.launch {
            while (true) {
                refreshItems()
                delay(1000)
            }
        }
    }

    private suspend fun refreshItems() {
        val count = pageService.getPageCount()
        if (count == stateNow.count) return
        if (stateNow.paused || firstVisibleIndex > 0) {
            setState { it.copy(count = count) }
            return
        }
        val items = pageService.getSourceInfos(stateNow.searchText, 100).sortedByDescending { it.pageId }
        val topId = items.firstOrNull()?.pageId ?: 0

        setState { it.copy(
            items = (items + stateNow.items).take(100),
            count = count,
            countShown = count,
            topId = topId,
            previousTopId = if (stateNow.topId > 0) { stateNow.topId } else { topId }
        ) }
    }

    fun togglePause() {
        setState { it.copy(paused = !it.paused) }
    }

    fun trackIndex(index: Int) {
        firstVisibleIndex = index
    }

    fun onSearch(searchText: String) {
        setState { it.copy(searchText = searchText) }
        viewModelScope.launch {
            refreshItems()
        }
    }
}

data class PageTableState(
    val items: List<PageInfo> = emptyList(),
    val count: Long = 0,
    val countShown: Long = 0,
    val topId: Long = 0,
    val previousTopId: Long = 0,
    val paused: Boolean = false,
    val searchText: String = "",
)