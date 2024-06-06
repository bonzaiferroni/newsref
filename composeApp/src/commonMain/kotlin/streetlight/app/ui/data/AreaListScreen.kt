package streetlight.app.ui.data

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.chopui.Scaffold
import streetlight.app.io.AreaDao
import streetlight.app.ui.core.DataList
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.Area

class AreaListScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<AreaListModel>()
        val state by screenModel.state

        DataList(
            title = "Areas",
            items = state.areas,
            provideName = { it.name },
            floatingAction = { navigator?.push(AreaCreatorScreen() {
                screenModel.updateHighlight(it.id)
                screenModel.fetchAreas()
            }) },
            navigator = navigator,
        )
    }
}

class AreaListModel(
    private val areaDao: AreaDao
) : UiModel<AreaListState>(AreaListState()) {
    init {
        fetchAreas()
    }

    fun fetchAreas() {
        screenModelScope.launch(Dispatchers.IO) {
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