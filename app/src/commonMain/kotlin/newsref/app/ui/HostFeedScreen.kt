package newsref.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
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
    LazyColumn {
        items(state.hosts) {
            Text(it.core)
        }
    }
}