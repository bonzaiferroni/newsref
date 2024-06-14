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
import streetlight.app.io.RequestDao
import streetlight.app.services.BusService
import streetlight.app.ui.core.DataEditor
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Request

@Composable
fun RequestEditorScreen(id: Int?, navigator: Navigator?) {
    val viewModel = koinViewModel<RequestEditorModel> { parametersOf(id) }
    val state by viewModel.state

    DataEditor(
        title = "Add Request",
        result = state.result,
        isComplete = state.isComplete,
        isCreate = id == null,
        createData = viewModel::createRequest,
        navigator = navigator,
    ) {

    }
}

class RequestEditorModel(
    private val id: Int?,
    private val requestDao: RequestDao,
    private val bus: BusService
) : UiModel<RequestEditorState>(RequestEditorState()) {

    init {
        if (id != null) {
            viewModelScope.launch(Dispatchers.IO) {
                requestDao.get(id)?.let { sv = sv.copy(request = it) }
            }
        }
    }

    fun createRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            if (id == null) {
                val id = requestDao.create(sv.request)
                val isFinished = id > 0
                sv = sv.copy(isComplete = isFinished, result = "id: $id")
            } else {
                val isComplete = requestDao.update(sv.request)
                sv = sv.copy(isComplete = isComplete, result = "update: $isComplete")
            }
            bus.supply(sv.request)
        }
    }
}

data class RequestEditorState(
    val request: Request = Request(),
    val isComplete: Boolean = false,
    val result: String = ""
) : UiState