package newsref.dashboard.ui.screens

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.dashboard.LocalNavigator
import newsref.dashboard.StoryItemRoute
import newsref.dashboard.StoryTableRoute
import newsref.dashboard.ui.table.*

@Composable
fun StoryTableScreen(
    route: StoryTableRoute,
    viewModel: StoryTableModel = viewModel { StoryTableModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val nav = LocalNavigator.current
    DataTable(
        name = "Stories",
        items = state.stories,
        columnGroups = groups(
            columns(
                TableColumn(
                    name = "Score", width = 60
                ) { TextCell(it.score)},
                TableColumn(
                    name = "Title", weight = 1f,
                    onClickCell = { nav.go(StoryItemRoute(it.id)) }
                ) { TextCell(it.title) },
                TableColumn(
                    name = "HpnAt", width = 60
                ) { DurationAgoCell(it.happenedAt)}
            ),
            columns(
                TableColumn(
                    name = "Size", width = 60
                ) { TextCell(it.size)},
            )
        )
    )
}