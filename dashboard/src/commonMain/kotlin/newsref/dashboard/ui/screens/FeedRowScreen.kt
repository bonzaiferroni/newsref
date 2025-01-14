package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import compose.icons.TablerIcons
import compose.icons.tablericons.ExternalLink
import compose.icons.tablericons.Globe
import kotlinx.coroutines.launch
import newsref.dashboard.FeedRowRoute
import newsref.dashboard.ui.table.CellControls
import newsref.dashboard.ui.table.PropertyRow
import newsref.dashboard.ui.table.PropertyTable
import newsref.dashboard.ui.table.TextCell
import newsref.dashboard.ui.table.TextFieldCell
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
            PropertyRow(
                name = "href",
                controls = listOf(CellControls(TablerIcons.ExternalLink) { uriHandler.openUri(item.url.toString()) })
            ) {
                TextFieldCell(state.updatedHref, viewModel::changeHref)
            }
        ))
    }
}
