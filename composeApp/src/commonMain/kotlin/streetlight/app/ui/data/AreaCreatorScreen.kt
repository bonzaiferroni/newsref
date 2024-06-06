package streetlight.app.ui.data

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.io.AreaDao
import streetlight.app.ui.core.DataCreator
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Area

class AreaCreatorScreen(
    private val onComplete: ((item: Area) -> Unit)?
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<AreaCreatorModel>()
        val state by screenModel.state

        DataCreator(
            title = "Add Area",
            item = state.area,
            result = state.result,
            isComplete = state.isComplete,
            onComplete = onComplete,
            createData = screenModel::createArea,
            navigator = navigator,
        ) {
            TextField(
                value = state.area.name,
                onValueChange = screenModel::updateName,
                label = { Text("Name") }
            )
        }
    }
}

class AreaCreatorModel(
    private val areaDao: AreaDao,
) : UiModel<AreaCreatorState>(AreaCreatorState()) {

    fun updateName(name: String) {
        sv = sv.copy(area = sv.area.copy(name = name))
    }

    fun createArea() {
        screenModelScope.launch(Dispatchers.IO) {
            val id = areaDao.create(sv.area)
            val isFinished = id > 0
            sv = sv.copy(
                result = "result: $id",
                area = sv.area.copy(id = id),
                isComplete = isFinished
            )
        }
    }
}

data class AreaCreatorState(
    val area: Area = Area(),
    val isComplete: Boolean = false,
    val result: String = ""
) : UiState