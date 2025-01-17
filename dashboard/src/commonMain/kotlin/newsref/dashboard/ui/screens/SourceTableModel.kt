
package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.services.SourceService
import newsref.model.dto.SourceInfo
import kotlin.time.Duration.Companion.minutes

class SourceTableModel(
    private val sourceService: SourceService = SourceService()
) : ScreenModel<SourceTableState>(SourceTableState()) {
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
        val items = sourceService.getSourceInfos(stateNow.topId, 100).sortedByDescending { it.sourceId }
        val topId = items.firstOrNull()?.sourceId ?: 0

        editState { it.copy(
            items = (items + stateNow.items).take(100),
            count = count,
            topId = topId
        ) }
    }
}

data class SourceTableState(
    val items: List<SourceInfo> = emptyList(),
    val count: Long = 0,
    val topId: Long = 0,
)