package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.*
import androidx.compose.material3.*
import newsref.app.blip.controls.TabPage
import newsref.app.blip.controls.TabPages
import newsref.app.blip.controls.pages
import newsref.dashboard.*
import newsref.dashboard.FeedItemRoute
import newsref.dashboard.LocalNavigator
import newsref.dashboard.SourceItemRoute
import newsref.dashboard.ui.controls.*
import newsref.dashboard.ui.table.*
import newsref.db.model.Feed
import java.io.File

@Composable
fun FeedItemScreen(
    route: FeedItemRoute,
    viewModel: FeedItemModel = viewModel { FeedItemModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val item = state.updatedFeed
    val uriHandler = LocalUriHandler.current
    val nav = LocalNavigator.current
    if (item == null) {
        Text("Fetching Feed with id: ${route.feedId}")
        return
    }

    FeedRowProperties(
        name = item.url.core,
        item = item,
        href = state.updatedHref,
        changeHref = viewModel::changeHref,
        changeUpdatedItem = viewModel::changeUpdatedItem
    )
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(halfSpacing)
        ) {
            val saveText = when (state.canUpdateItem) {
                true -> "Save"
                false -> "Saved"
            }
            Button(onClick = viewModel::updateItem, enabled = state.canUpdateItem) { Text(saveText) }
            Button(onClick = viewModel::checkFeed) { Text("Check Feed")}
        }
        Countdown(state.nextRefresh)
    }
    TabPages(
        currentPageName = state.page,
        onChangePage = viewModel::changePage,
        pages = pages(
            TabPage(name = "Sources", scrollbar = false) {
                SourceTable(
                    sources = state.sourceInfos
                )
            },
            TabPage(name = "Leads", scrollbar = false) {
                DataTable(
                    name = "LeadInfos",
                    items = state.leadInfos,
                    glowFunction = { glowOverHour(it.freshAt) },
                    columnGroups = listOf(
                        columns(
                            TableColumn("Headline") { TextCell(it.feedHeadline) { uriHandler.openUri(it.url.href) } },
                        ),
                        columns(
                            TableColumn("Fresh", 100, AlignCell.Right) { DurationAgoCell(it.freshAt) },
                            TableColumn("Attempt", 100, AlignCell.Right) { DurationAgoCell(it.lastAttemptAt) },
                            TableColumn("Ext", 50) { BooleanCell(it.isExternal) },
                            TableColumn("Links", 50, AlignCell.Right) { TextCell(it.linkCount.toString()) },
                            TableColumn("Src", 50) { NullableIdCell(it.targetId) { nav.go(SourceItemRoute(it)) } },
                            TableColumn("Pos", 50, AlignCell.Right) { TextCell(it.feedPosition) }
                        )
                    )
                )
            }
        )
    )
}

@Composable
fun FeedRowProperties(
    name: String,
    item: Feed,
    href: String,
    changeHref: (String) -> Unit,
    changeUpdatedItem: (Feed) -> Unit,
) {
    PropertyTable(
        name = name,
        item = item,
        properties = listOf(
            PropertyRow<Feed>(
                name = "href", controls = listOf(openExternalLink { it.url.href })
            ) { TextFieldCell(href, changeHref) },
            PropertyRow<Feed>(
                name = "selector"
            ) { TextFieldCell(item.selector.toString()) { changeUpdatedItem(item.copy(selector = it)) } },
            PropertyRow<Feed>(
                name = "external"
            ) { BooleanCell(item.external) { changeUpdatedItem(item.copy(external = it)) } },
            PropertyRow<Feed>(
                name = "track position"
            ) { BooleanCell(item.trackPosition) { changeUpdatedItem(item.copy(trackPosition = it)) } },
            PropertyRow<Feed>(
                name = "debug",
                controls = listOf(openExternalLink { "file://${File("../cache/AnchorFinder/debug.html").absolutePath}" })
            ) { BooleanCell(item.debug) { changeUpdatedItem(item.copy(debug = it)) } },
            PropertyRow<Feed>(
                name = "disabled"
            ) { BooleanCell(item.disabled) { changeUpdatedItem(item.copy(disabled = it)) } },
            PropertyRow<Feed>(
                name = "Check at"
            ) { DurationUntilCell(item.checkAt)},
            PropertyRow<Feed>(
                name = "note"
            ) { TextFieldCell(item.note) { changeUpdatedItem(item.copy(note = it)) } },
        )
    )
}
