package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import newsref.dashboard.StartRoute
import newsref.db.services.SourceService
import newsref.model.dto.SourceInfo


class StartModel(
    route: StartRoute,
    sourceService: SourceService = SourceService()
) : StateModel<StartState>(StartState()) {

    init {
        viewModelScope.launch {
            val sources = sourceService.getTopSourceInfos()
            setState { it.copy(sources = sources) }
        }
    }
}

data class StartState(
    val sources: List<SourceInfo> = emptyList()
)