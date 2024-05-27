package streetlight.app.ui

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.data.AreaDao
import streetlight.app.ui.abstract.UiModel
import streetlight.app.ui.abstract.UiState
import streetlight.model.Area

class CreateAreaModel(
    private val areaDao: AreaDao,
) : UiModel<CreateAreaState>(CreateAreaState()) {

    fun updateName(name: String) {
        sv = sv.copy(area = sv.area.copy(name = name))
    }

    fun addArea() {
        screenModelScope.launch(Dispatchers.IO) {
            val result = areaDao.addArea(sv.area)
            sv = sv.copy(result = result, area = Area())
        }
    }
}

data class CreateAreaState(
    val area: Area = Area(),
    val result: String = ""
) : UiState