package newsref.dashboard.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import newsref.dashboard.FeedRowRoute
import newsref.dashboard.ui.table.CountCell
import newsref.dashboard.ui.table.DataTable
import newsref.dashboard.ui.table.TableColumn
import newsref.dashboard.ui.table.TextCell

@Composable
fun FeedTableScreen(
    navController: NavController,
    viewModel: FeedTableModel = viewModel { FeedTableModel() }
) {
    val state by viewModel.state.collectAsState()
    FeedRowProperties(
        name = "New Feed",
        item = state.newItem,
        href = state.newHref,
        changeHref = viewModel::changeHref,
        changeSelector = viewModel::changeSelector,
        changeExternal = viewModel::changeExternal,
    )
    Button(onClick = viewModel::addNewItem, enabled = state.canAddItem) {
        Text("Add")
    }
    DataTable(
        name = "Feed Table",
        newItems = state.newItems,
        oldItems = state.oldItems,
        onClickRow = { navController.navigate(FeedRowRoute(it.id))},
        columns = listOf(
            TableColumn(name = "Core", width = 200) { TextCell(it.url.core) },
            TableColumn(name = "Leads", width = 200) { CountCell(it.id, state.leadCounts, state.leadAdditions) }
        )
    )
}