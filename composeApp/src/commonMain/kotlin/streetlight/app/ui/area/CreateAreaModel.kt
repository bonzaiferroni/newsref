package streetlight.app.ui.area

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
            val id = areaDao.createArea(sv.area)
            val isFinished = id > 0
            sv = sv.copy(
                result = "result: $id",
                area = sv.area.copy(id = id),
                isFinished = isFinished
            )
        }
    }
}

data class CreateAreaState(
    val area: Area = Area(),
    val isFinished: Boolean = false,
    val result: String = ""
) : UiState