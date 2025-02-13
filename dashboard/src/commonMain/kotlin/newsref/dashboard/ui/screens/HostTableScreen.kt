package newsref.dashboard.ui.screens

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.dashboard.HostItemRoute
import newsref.dashboard.HostTableRoute
import newsref.dashboard.LocalNavigator
import newsref.dashboard.ui.table.*
import newsref.dashboard.utils.shortFormat

@Composable
fun HostTableScreen(
    route: HostTableRoute,
    viewModel: HostTableModel = viewModel { HostTableModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val nav = LocalNavigator.current

    DataTable(
        name = "Hosts",
        items = state.hosts,
        searchText = state.search,
        onSearch = {
            nav.setRoute(route.copy(searchText = it))
            viewModel.changeSearch(it)
        },
        columnGroups = listOf(
            columns(
                TableColumn(
                    name = "Score", width = 60, align = AlignCell.Right
                ) { TextCell(it.score.shortFormat()) },
                TableColumn(
                    name = "Core", weight = 1f,
                    onClickCell = { nav.go(HostItemRoute(it.id)) }
                ) { TextCell(it.core) }
            )
        )
    )
}