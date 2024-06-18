package streetlight.app.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.Scenes
import streetlight.app.chopui.BoxScaffold
import streetlight.app.io.EventDao
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.dto.EventInfo

@Composable
fun NowScreen(navigator: Navigator?) {
    val screenModel = koinViewModel<NowModel>()
    val state by screenModel.state

    BoxScaffold(
        title = "Now",
        navigator = navigator,
    ) {
        Column {
            Button(onClick = { Scenes.debug.go(navigator) }) {
                Text("Debug")
            }
            LazyColumn {
                items(state.events) { event ->
                    EventCard(event = event, onClick = { Scenes.eventProfile.go(navigator, event.id) })
                }
            }
        }
    }
}

@Composable
fun EventCard(event: EventInfo, onClick: () -> Unit) {
    Card(onClick = onClick) {
        Row(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(event.locationName)
        }
    }
}

class NowModel(
    private val eventDao: EventDao,
) : UiModel<NowState>(NowState()) {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val events = eventDao.getAllInfo()
            sv = sv.copy(events = events)
        }
    }
}

data class NowState(
    val events: List<EventInfo> = emptyList(),
) : UiState