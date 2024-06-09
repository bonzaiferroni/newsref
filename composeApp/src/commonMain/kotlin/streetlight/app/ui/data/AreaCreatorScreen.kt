package streetlight.app.ui.data

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.core.parameter.parametersOf
import streetlight.app.io.AreaDao
import streetlight.app.services.BusService
import streetlight.app.ui.core.DataEditor
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Area

@Composable
fun AreaCreatorScreen(
    id: Int?,
    navigator: Navigator?
) {
    val viewModel = koinViewModel<AreaCreatorModel> { parametersOf(id) }
    val state by viewModel.state

    DataEditor(
        title = "Add Area",
        result = state.result,
        isComplete = state.isComplete,
        isCreate = id == null,
        createData = viewModel::createArea,
        navigator = navigator,
    ) {
        TextField(
            value = state.area.name,
            onValueChange = viewModel::updateName,
            label = { Text("Name") }
        )
    }
}

class AreaCreatorModel(
    private val id: Int?,
    private val areaDao: AreaDao,
    private val bus: BusService
) : UiModel<AreaCreatorState>(AreaCreatorState()) {

    init {
        if (id != null) {
            viewModelScope.launch(Dispatchers.IO) {
                areaDao.get(id)?.let {
                    sv = sv.copy(area = it)
                }
            }
        }
    }

    fun updateName(name: String) {
        sv = sv.copy(area = sv.area.copy(name = name))
    }

    fun createArea() {
        viewModelScope.launch(Dispatchers.IO) {
            if (sv.area.id == 0) {
                val id = areaDao.create(sv.area)
                val isFinished = id > 0
                sv = sv.copy(
                    result = "result: $id",
                    area = sv.area.copy(id = id),
                    isComplete = isFinished
                )
            } else {
                val isComplete = areaDao.update(sv.area)
                sv = sv.copy(
                    result = "result: $isComplete",
                    isComplete = isComplete
                )
            }
            bus.supply(sv.area)
        }
    }
}

data class AreaCreatorState(
    val area: Area = Area(),
    val isComplete: Boolean = false,
    val result: String = ""
) : UiState