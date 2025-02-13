package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.dashboard.*
import newsref.dashboard.nav.LocalNavigator
import newsref.dashboard.ui.controls.*
import newsref.dashboard.ui.table.*
import newsref.dashboard.utils.shortFormat

@Composable
fun HostItemScreen(
    route: HostItemRoute,
    viewModel: HostItemModel = viewModel { HostItemModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val host = state.host
    if (host == null) {
        Text("Fetching data for host: ${state.hostId}")
        return
    }
    val nav = LocalNavigator.current
    TabPages(
        currentPageName = state.page,
        onChangePage = {
            nav.setRoute(route.copy(page = it))
            viewModel.changePage(it)
        },
        pages = pages(
            TabPage("Properties") {
                PropertyTable(
                    name = host.core,
                    item = host,
                    properties = listOf(
                        textRow("Core", host.core),
                        textRow("Name", host.name),
                        textRow("Score", host.score.shortFormat())
                    )
                )
            },
            TabPage("Sources", false) {
                CloudChart(
                    state.clouds,
                    300.dp,
                    viewModel::changeSelected
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SinceMenu(state.since, viewModel::changeSince)
                    Button(onClick = { state.sources.toSpeakRoute()?.let { nav.go(it) } }) { Text("Speak") }
                }
                SourceTable(
                    sources = state.sources,
                    searchText = state.searchText,
                    scrollId = state.selectedId,
                    onSearch = viewModel::onSearch,
                    onSorting = viewModel::changeSorting,
                )
            }
        )
    )
}