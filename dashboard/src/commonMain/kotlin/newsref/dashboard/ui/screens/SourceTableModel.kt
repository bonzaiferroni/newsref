
package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.db.services.SourceService
import newsref.model.dto.SourceInfo

class SourceTableModel(
    private val sourceService: SourceService = SourceService()
) : ScreenModel<SourceTableState>(SourceTableState()) {

    private var firstVisibleIndex = 0

    init {
        viewModelScope.launch {
            while (true) {
                refreshItems()
                delay(200)
            }
        }
    }

    private suspend fun refreshItems() {
        val count = sourceService.getSourceCount()
        if (count == stateNow.count) return
        if (stateNow.paused || firstVisibleIndex > 0) {
            setState { it.copy(count = count) }
            return
        }
        val items = sourceService.getSourceInfos(stateNow.topId, 100).sortedByDescending { it.sourceId }
        val topId = items.firstOrNull()?.sourceId ?: 0

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
}

data class SourceTableState(
    val items: List<SourceInfo> = emptyList(),
    val count: Long = 0,
    val countShown: Long = 0,
    val topId: Long = 0,
    val previousTopId: Long = 0,
    val paused: Boolean = false,
)