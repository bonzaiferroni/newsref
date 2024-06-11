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
import streetlight.app.io.PerformanceDao
import streetlight.app.services.BusService
import streetlight.app.ui.core.DataEditor
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Performance

@Composable
fun PerformanceEditorScreen(id: Int?, navigator: Navigator?) {
    val viewModel = koinViewModel<PerformanceEditorModel> { parametersOf(id) }
    val state by viewModel.state

    DataEditor(
        title = "Add Performance",
        result = state.result,
        isComplete = state.isComplete,
        isCreate = id == null,
        createData = viewModel::createPerformance,
        navigator = navigator,
    ) {
        TextField(
            value = state.performance.name,
            onValueChange = viewModel::updateName,
            label = { Text("Name") }
        )
    }
}

class PerformanceEditorModel(
    private val id: Int?,
    private val performanceDao: PerformanceDao,
    private val bus: BusService
) : UiModel<PerformanceEditorState>(PerformanceEditorState()) {

    init {
        if (id != null) {
            viewModelScope.launch(Dispatchers.IO) {
                performanceDao.get(id)?.let { sv = sv.copy(performance = it) }
            }
        }
    }

    fun updateName(name: String) {
        sv = sv.copy(performance = sv.performance.copy(name = name))
    }

    fun createPerformance() {
        viewModelScope.launch(Dispatchers.IO) {
            if (id == null) {
                val id = performanceDao.create(sv.performance)
                val isFinished = id > 0
                sv = sv.copy(isComplete = isFinished, result = "id: $id")
            } else {
                val isComplete = performanceDao.update(sv.performance)
                sv = sv.copy(isComplete = isComplete, result = "update: $isComplete")
            }
            bus.supply(sv.performance)
        }
    }
}

data class PerformanceEditorState(
    val performance: Performance = Performance(userId = 1),
    val isComplete: Boolean = false,
    val result: String = ""
) : UiState