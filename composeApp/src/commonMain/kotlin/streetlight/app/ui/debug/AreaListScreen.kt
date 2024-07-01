package streetlight.app.ui.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.Scenes
import streetlight.app.io.AreaDao
import streetlight.app.services.BusService
import streetlight.app.ui.debug.controls.DataList
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Area

@Composable
fun AreaListScreen(navigator: Navigator?) {
    val screenModel = koinViewModel(AreaListModel::class)
    val state by screenModel.state

    DataList(
        title = "Areas",
        items = state.areas,
        provideName = { it.name },
        floatingAction = {
            screenModel.requestArea()
            Scenes.areaEditor.go(navigator)
        },
        navigator = navigator,
        onEdit = { Scenes.areaEditor.go(navigator, it.id) },
        onDelete = screenModel::deleteArea,
    )
}

class AreaListModel(
    private val areaDao: AreaDao,
    private val bus: BusService,
) : UiModel<AreaListState>(AreaListState()) {
    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val areas = areaDao.getAll()
            sv = sv.copy(areas = areas)
        }
    }

    fun requestArea() {
        bus.request<Area> {
            refresh()
        }
    }

    fun deleteArea(area: Area) {
        viewModelScope.launch(Dispatchers.IO) {
            areaDao.delete(area.id)
            refresh()
        }
    }
}

data class AreaListState(
    val areas: List<Area> = emptyList(),
    val result: String = "",
    val highlightId: Int = -1
) : UiState
