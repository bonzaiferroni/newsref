package streetlight.app.ui.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.io.AreaDao
import streetlight.app.ui.core.DataList
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Area

@Composable
fun AreaListScreen() {
    val navigator = rememberNavigator()
    val screenModel = koinViewModel(AreaListModel::class)
    val state by screenModel.state

    DataList(
        title = "Areas",
        items = state.areas,
        provideName = { it.name },
        floatingAction = { navigator.navigate("/createArea")},
        navigator = navigator,
    )
}

class AreaListModel(
    private val areaDao: AreaDao
) : UiModel<AreaListState>(AreaListState()) {
    init {
        fetchAreas()
    }

    fun fetchAreas() {
        viewModelScope.launch(Dispatchers.IO) {
            val areas = areaDao.getAll()
            sv = sv.copy(areas = areas)
        }
    }

    fun updateHighlight(id: Int) {
        sv = sv.copy(highlightId = id)
    }
}

data class AreaListState(
    val areas: List<Area> = emptyList(),
    val result: String = "",
    val highlightId: Int = -1
) : UiState