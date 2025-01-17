package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import compose.icons.TablerIcons
import compose.icons.tablericons.ExternalLink
import androidx.compose.ui.Alignment
import kotlinx.datetime.Clock
import androidx.compose.material3.*
import kotlinx.serialization.Serializable
import newsref.dashboard.*
import newsref.dashboard.ui.controls.*
import newsref.dashboard.ui.table.*
import newsref.model.data.*
import kotlin.time.Duration.Companion.minutes

@Serializable
data class FeedItemRoute(val feedId: Int) : ScreenRoute("Feed Item")

@Composable
fun FeedItemScreen(
    route: FeedItemRoute,
    navController: NavController,
    viewModel: FeedItemModel = viewModel { FeedItemModel(route) }
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
            changeTrackPosition = viewModel::changeTrackPosition
        )
        Row(horizontalArrangement = Arrangement.spacedBy(halfSpacing), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = viewModel::updateItem, enabled = state.canUpdateItem) {
                Text("Update")
            }
            ConfirmButton("Delete", onConfirm = { viewModel.deleteFeed { navController.navigate(FeedTableRoute) }})
            Countdown(Clock.System.now() + 1.minutes)
        }
        DataTable(
            name = "LeadInfos",
            rows = state.leadInfos,
            glowFunction = { glowOverHour(it.freshAt) },
            columns = listOf(
                TableColumn("Headline") { TextCell(it.feedHeadline) { uriHandler.openUri(it.url.href) } },
                TableColumn("Fresh", 100, AlignContent.Right) { DurationAgoCell(it.freshAt) },
                TableColumn("Attempt", 100, AlignContent.Right) { DurationAgoCell(it.lastAttemptAt) },
                TableColumn("Ext", 50) { BooleanCell(it.isExternal) },
                TableColumn("Links", 50, AlignContent.Right) { TextCell(it.linkCount.toString())},
                TableColumn("Src", 50) { NullableIdCell(it.targetId) { navController.navigate(SourceItemRoute(it)) } },
                TableColumn("Pos", 50, AlignContent.Right) { TextCell(it.feedPosition) }
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
    changeTrackPosition: (Boolean) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    PropertyTable(
        name = name,
        item = item,
        properties = listOf(
            PropertyRow<Feed>("href") { TextFieldCell(href, changeHref) }
                .addControl(TablerIcons.ExternalLink) { uriHandler.openUri(item.url.toString()) },
            PropertyRow(name = "selector") { TextFieldCell(item.selector, changeSelector) },
            PropertyRow("external") { BooleanCell(item.external, changeExternal) },
            PropertyRow("track position") { BooleanCell(item.trackPosition, changeTrackPosition)}
        )
    )
}
