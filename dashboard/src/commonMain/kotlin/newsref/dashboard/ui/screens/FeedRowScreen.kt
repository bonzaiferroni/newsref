package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import compose.icons.TablerIcons
import compose.icons.tablericons.ExternalLink
import newsref.dashboard.FeedRowRoute
import newsref.dashboard.FeedTableRoute
import newsref.dashboard.halfSpacing
import newsref.dashboard.ui.controls.ConfirmButton
import newsref.dashboard.ui.table.BooleanCell
import newsref.dashboard.ui.table.CellControls
import newsref.dashboard.ui.table.DataTable
import newsref.dashboard.ui.table.DurationAgoCell
import newsref.dashboard.ui.table.PropertyRow
import newsref.dashboard.ui.table.PropertyTable
import newsref.dashboard.ui.table.TableColumn
import newsref.dashboard.ui.table.TextCell
import newsref.dashboard.ui.table.TextFieldCell
import newsref.dashboard.ui.table.glowOverDay
import newsref.dashboard.ui.table.glowOverHour
import newsref.model.data.Feed

@Composable
fun FeedRowScreen(
    route: FeedRowRoute,
    navController: NavController,
    viewModel: FeedRowModel = viewModel { FeedRowModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val item = state.updatedFeed
    val uriHandler = LocalUriHandler.current
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
        Row(horizontalArrangement = Arrangement.spacedBy(halfSpacing)) {
            Button(onClick = viewModel::updateItem, enabled = state.canUpdateItem) {
                Text("Update")
            }
            ConfirmButton("Delete", onConfirm = { viewModel.deleteFeed { navController.navigate(FeedTableRoute) }})
        }
        DataTable(
            name = "LeadInfos",
            rows = state.leadInfos,
            glowFunction = { glowOverHour(it.freshAt) },
            columns = listOf(
                TableColumn("Headline", 300, { uriHandler.openUri(it.url.href)}) { TextCell(it.feedHeadline.toString()) },
                TableColumn("Fresh At", 200) { DurationAgoCell(it.freshAt) },
                TableColumn("Last Attempt", 200) { DurationAgoCell(it.lastAttemptAt) },
                TableColumn("External", 50) { BooleanCell(it.isExternal) },
                TableColumn("Links", 50) { TextCell(it.linkCount.toString())}
            )
        )
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
