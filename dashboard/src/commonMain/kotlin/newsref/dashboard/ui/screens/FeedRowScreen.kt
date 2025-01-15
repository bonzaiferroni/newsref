package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
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
import newsref.dashboard.ui.table.BooleanCell
import newsref.dashboard.ui.table.CellControls
import newsref.dashboard.ui.table.DataTable
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
    val item = state.updatedFeed
    if (item == null) {
        Text("Fetching Feed with id: ${route.feedId}")
    } else {
        FeedRowProperties(
            name = item.url.core,
            item = item,
            href = state.updatedHref,
            changeHref = viewModel::changeHref,
            changeSelector = viewModel::changeSelector,
            changeExternal = viewModel::changeExternal,
        )
        Button(onClick = viewModel::updateItem, enabled = state.canUpdateItem) {
            Text("Update")
        }
        // DataTable("LeadJobs", )
    }
}

@Composable
fun FeedRowProperties(
    name: String,
    item: Feed,
    href: String,
    changeHref: (String) -> Unit,
    changeSelector: (String) -> Unit,
    changeExternal: (Boolean) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    PropertyTable(
        name = name,
        item = item,
        properties = listOf(
            PropertyRow(
                name = "href",
                controls = listOf(CellControls(TablerIcons.ExternalLink) { uriHandler.openUri(item.url.toString()) })
            ) { TextFieldCell(href, changeHref) },
            PropertyRow(name = "selector") { TextFieldCell(item.selector, changeSelector) },
            PropertyRow("external") { BooleanCell(item.external, changeExternal) }
        )
    )
}
