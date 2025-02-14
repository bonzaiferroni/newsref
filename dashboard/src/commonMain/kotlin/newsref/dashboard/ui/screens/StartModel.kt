package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.app.blip.core.StateModel
import newsref.dashboard.StartRoute
import newsref.db.services.SourceService
import newsref.model.dto.SourceInfo
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days


class StartModel(
    route: StartRoute,
    private val sourceService: SourceService = SourceService()
) : StateModel<StartState>(StartState(route.days.days)) {

    init {
        refreshItems()
    }

    fun changeSince(duration: Duration) {
        setState { it.copy(since = duration) }
        refreshItems()
    }

    private fun refreshItems() {
        viewModelScope.launch {
            val sources = sourceService.getTopSourceInfos(stateNow.since)
            setState { it.copy(sources = sources) }
        }
    }
}

data class StartState(
    val since: Duration,
    val sources: List<SourceInfo> = emptyList(),
)