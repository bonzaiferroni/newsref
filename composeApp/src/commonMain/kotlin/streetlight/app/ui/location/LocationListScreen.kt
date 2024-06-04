package streetlight.app.ui.location

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import streetlight.app.chopui.BoxScaffold
import streetlight.app.chopui.Scaffold

class LocationListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<LocationListModel>()
        val state by screenModel.state
        BoxScaffold(
            title = "Locations",
            navigator = navigator,
            floatingAction = { navigator?.push(CreateLocationScreen() {
                // screenModel.updateHighlight(it)
                screenModel.fetchLocations()
            }) }
        ) {
            LazyColumn {
                items(state.locations) {
                    val area = state.areas.find { area -> area.id == it.areaId }
                    Row {
                        Text(it.name)
                        area?.let { Text(" (${it.name})") }
                    }
                }
            }
        }
    }
}