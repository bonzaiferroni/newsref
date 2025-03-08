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
import newsref.app.model.Host

@Composable
fun HostFeedScreen(
    route: HostFeedRoute,
    viewModel: HostFeedModel = viewModel { HostFeedModel(route) }
) {
    val state by viewModel.state.collectAsState()
    LazyColumn(
        verticalArrangement = Blip.ruler.columnTight
    ) {
        items(state.hosts) {
            HostItem(it)
        }
    }
}

@Composable
fun HostItem(
    host: Host
) {
    val nav = LocalNav.current
    Row(
        horizontalArrangement = Blip.ruler.rowTight,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .clickable { nav.go(HostRoute(host.id, host.core)) }
    ) {
        val color = Blip.colors.getSwatchFromIndex(host.id)
        ShapeImage(url = host.logo, color = color, modifier = Modifier.height(48.dp))
        Column(
            verticalArrangement = Blip.ruler.columnTight,
        ) {
            H2(host.name ?: host.core)
            Label("Visibility: ${host.score}")
        }
    }
}