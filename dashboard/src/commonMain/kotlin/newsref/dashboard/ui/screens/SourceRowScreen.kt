package newsref.dashboard.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.datetime.Clock
import androidx.compose.material3.*
import newsref.dashboard.*
import newsref.dashboard.ui.controls.*
import newsref.dashboard.ui.table.*
import newsref.model.data.*
import newsref.db.services.SourceService
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

data class SourceRowRoute(
    val id: Long
)

@Composable
fun SourceRowScreen(
    sourceRowRoute: SourceRowRoute,
    navController: NavController,
    viewModel: SourceRowModel = viewModel { SourceRowModel(sourceRowRoute) }
) {
    val state by viewModel.state.collectAsState()
}

class SourceRowModel(
    sourceRowRoute: SourceRowRoute,
    private val sourceService: SourceService = SourceService(),
) : ScreenModel<SourceRowState>(SourceRowState(sourceRowRoute.id)) {
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
    val source: Source? = null,
)