package newsref.dashboard.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import newsref.dashboard.FeedRowRoute
import newsref.dashboard.ui.table.PropertyRow
import newsref.dashboard.ui.table.PropertyTable
import newsref.db.services.FeedService
import newsref.model.data.Feed

@Composable
fun FeedRowScreen(
    route: FeedRowRoute,
    navController: NavController,
    viewModel: FeedRowModel = viewModel { FeedRowModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val uriHandler = LocalUriHandler.current
    val item = state.feed
    if (item == null) {
        Text("Fetching Feed with id: ${route.feedId}")
    } else {
        PropertyTable(item.url.core, item, listOf(
            PropertyRow("href", { uriHandler.openUri(item.url.toString())}) {
                Text(item.url.toString())
            }
        ))
    }
}

class FeedRowModel(
    route: FeedRowRoute,
    feedService: FeedService = FeedService(),
) : ScreenModel<FeedRowState>(FeedRowState(route.feedId)) {
    init {
        viewModelScope.launch {
            val feed = feedService.read(route.feedId)
            sv = sv.copy(feed = feed)
        }
    }
}

data class FeedRowState(
    val feedId: Int,
    val feed: Feed? = null
)
