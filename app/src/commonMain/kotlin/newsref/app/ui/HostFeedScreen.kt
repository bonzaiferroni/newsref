package newsref.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.*
import pondui.ui.controls.*
import pondui.ui.nav.LazyScaffold
import pondui.ui.nav.LocalNav
import pondui.ui.theme.Pond
import newsref.model.data.Host

@Composable
fun HostFeedScreen(
    route: HostFeedRoute,
    viewModel: HostFeedModel = viewModel { HostFeedModel(route) }
) {
    val state by viewModel.state.collectAsState()

    LazyScaffold {
        item {
            TextField(state.searchText, viewModel::changeSearchText)
        }

        items(state.pinnedHosts) {
            HostItem(it, true, viewModel::togglePin)
        }

        items(state.hosts) {
            HostItem(it, false, viewModel::togglePin)
        }
    }
}

@Composable
fun HostItem(
    host: Host,
    isPinned: Boolean,
    togglePin: (Int) -> Unit
) {
    val nav = LocalNav.current
    Row(
        horizontalArrangement = Pond.ruler.rowUnit,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .clickable { nav.go(HostRoute(host.id, host.core)) }
    ) {
        val color = Pond.colors.getSwatchFromIndex(host.id)
        ShapeImage(url = host.logo, color = color, modifier = Modifier.height(48.dp))
        Column(
            verticalArrangement = Pond.ruler.columnUnit,
        ) {
            Row(
                horizontalArrangement = Pond.ruler.rowUnit
            ) {
                H2(host.name ?: host.core, modifier = Modifier.weight(1f))
                Button(onClick = { togglePin(host.id) }) {
                    Text(if (isPinned) "Unpin" else "Pin")
                }
            }
            PropertyLabel("Visibility", host.score)
        }
    }
}