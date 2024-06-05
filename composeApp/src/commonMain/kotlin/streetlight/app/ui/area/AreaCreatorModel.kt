package streetlight.app.ui.area

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.data.AreaDao
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Area

class AreaCreatorModel(
    private val areaDao: AreaDao,
) : UiModel<AreaCratorState>(AreaCratorState()) {

    fun updateName(name: String) {
        sv = sv.copy(area = sv.area.copy(name = name))
    }

    fun addArea() {
        screenModelScope.launch(Dispatchers.IO) {
            val id = areaDao.create(sv.area)
            val isFinished = id > 0
            sv = sv.copy(
                result = "result: $id",
                area = sv.area.copy(id = id),
                isFinished = isFinished
            )
        }
    }
}

data class AreaCratorState(
    val area: Area = Area(),
    val isFinished: Boolean = false,
    val result: String = ""
) : UiState