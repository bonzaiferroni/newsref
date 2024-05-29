package streetlight.app.ui.event

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import streetlight.app.chopui.BoxScaffold
import streetlight.app.chopui.Scaffold

class EventListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<EventListModel>()
        val state by screenModel.state
        BoxScaffold(
            title = "Events",
            navigator = navigator,
            floatingAction = { navigator?.push(CreateEventScreen() {
                // screenModel.updateHighlight(it)
                screenModel.fetchEvents()
            }) }
        ) {
            LazyColumn {
                items(state.events) {
                    Row {
                        // Text(it.name)
                    }
                }
            }
        }
    }
}