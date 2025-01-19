package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.dashboard.ScreenRoute
import newsref.db.services.SourceService
import newsref.model.dto.SourceInfo

@Serializable
data class SourceItemRoute(
    val sourceId: Long,
    val pageName: String = "",
) : ScreenRoute("Source Item")

class SourceItemModel(
    route: SourceItemRoute,
    private val sourceService: SourceService = SourceService(),
) : ScreenModel<SourceRowState>(
    SourceRowState(
        sourceId = route.sourceId,
        page = route.pageName
    )
) {
    init {
        viewModelScope.launch {
            refreshItem()
            delay(stateNow.nextRefresh - Clock.System.now())
        }
    }

    private suspend fun refreshItem() {
        val source = sourceService.getSourceInfo(stateNow.sourceId)
        setState {
            it.copy(
                source = source
            )
        }
    }

    fun changePage(page: String) {
        setState { it.copy(page = page) }
    }
}

data class SourceRowState(
    val sourceId: Long,
    val nextRefresh: Instant = Instant.DISTANT_PAST,
    val source: SourceInfo? = null,
    val page: String = "",
)