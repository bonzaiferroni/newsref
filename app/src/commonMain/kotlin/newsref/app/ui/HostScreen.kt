package newsref.app.ui

import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.*
import newsref.app.blip.controls.*

@Composable
fun HostScreen(
    route: HostRoute,
    viewModel: HostModel = viewModel { HostModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val host = state.host
    if (host == null) return
    H1(host.core)
    TabCard(
        currentTab = state.tab,
        onChangePage = viewModel::changeTab,
        pages = pages(
            TabPage(name = "Feed", scrollbar = false) {
                LazyColumn {
                    items(state.sources) {
                        SourceBitItem(it)
                    }
                }
            }
        )
    )
}