package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.services.SourceService
import newsref.model.dto.SourceInfo

class SourceItemModel(
    sourceItemRoute: SourceItemRoute,
    private val sourceService: SourceService = SourceService(),
) : ScreenModel<SourceRowState>(SourceRowState(sourceItemRoute.sourceId)) {
    init {
        viewModelScope.launch {
            refreshItem()
            delay(stateNow.nextRefresh - Clock.System.now())
        }
    }

    private suspend fun refreshItem() {
        val source = sourceService.getSourceInfo(stateNow.sourceId)
        editState { it.copy(
            source = source
        ) }
    }
}

data class SourceRowState(
    val sourceId: Long,
    val nextRefresh: Instant = Instant.DISTANT_PAST,
    val source: SourceInfo? = null,
)