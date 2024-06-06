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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import streetlight.app.chopui.Scaffold

class AreaListScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = rememberScreenModel<AreaListModel>()
        val state by screenModel.state
        Scaffold(
            title = "Areas",
            navigator = navigator,
            floatingAction = { navigator?.push(AreaCreatorScreen() {
                screenModel.updateHighlight(it.id)
                screenModel.fetchAreas()
            })}
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn {
                    items(state.areas) { area ->
                        val fontWeight = if (area.id == state.highlightId) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                        Text(area.name, fontWeight = fontWeight)
                    }
                }
            }
        }
    }
}