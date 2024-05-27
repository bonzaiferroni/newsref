package streetlight.app.ui.area

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.data.AreaDao
import streetlight.app.ui.abstract.UiModel
import streetlight.app.ui.abstract.UiState
import streetlight.model.Area

class AreaListModel(
    private val areaDao: AreaDao
) : UiModel<AreaListState>(AreaListState()) {
    init {
        fetchAreas()
    }

    fun fetchAreas() {
        screenModelScope.launch(Dispatchers.IO) {
            val areas = areaDao.fetchAreas()
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