package newsref.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.nav.LocalNav
import newsref.app.blip.theme.Blip

@Composable
fun HostFeedScreen(
    route: HostFeedRoute,
    viewModel: HostFeedModel = viewModel { HostFeedModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val nav = LocalNav.current
    LazyColumn(
        verticalArrangement = Blip.ruler.columnTight
    ) {
        items(state.hosts) {
            Row(
                horizontalArrangement = Blip.ruler.rowTight,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { nav.go(HostRoute(it.id, it.core)) }
            ) {
                val color = Blip.colors.getSwatchFromIndex(it.id)
                ShapeImage(url = it.logo, color = color, modifier = Modifier.height(48.dp))
                H2(it.core)
            }
        }
    }
}