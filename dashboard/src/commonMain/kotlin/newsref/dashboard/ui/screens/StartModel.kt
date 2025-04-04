package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.app.blip.core.StateModel
import newsref.dashboard.StartRoute
import newsref.db.services.PageService
import newsref.model.data.PageInfo
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days


class StartModel(
    route: StartRoute,
    private val pageService: PageService = PageService()
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
            val sources = pageService.getTopSourceInfos(stateNow.since)
            setState { it.copy(sources = sources) }
        }
    }
}

data class StartState(
    val since: Duration,
    val sources: List<PageInfo> = emptyList(),
)