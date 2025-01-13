package newsref.dashboard.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import newsref.model.data.Feed

@Composable
fun FeedTableScreen(
    navController: NavController,
    viewModel: FeedTableModel = viewModel { FeedTableModel() }
) {
    val state by viewModel.uiState.collectAsState()
    DataTable(
        name = "Feed Table",
        items = state.feedItems,
        columns = listOf(
            TableColumn(name = "Core", width = 200) { TextCell(it.url.core) },
            TableColumn(name = "Leads", width = 200) { CountCell(it.id, state.leadCounts, state.leadAdditions) }
        )
    )
}