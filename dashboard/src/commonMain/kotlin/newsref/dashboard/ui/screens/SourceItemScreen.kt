package newsref.dashboard.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import newsref.dashboard.ScreenRoute
import newsref.dashboard.ui.table.DurationAgoCell
import newsref.dashboard.ui.table.PropertyRow
import newsref.dashboard.ui.table.PropertyTable
import newsref.dashboard.ui.table.TextCell

@Serializable
data class SourceItemRoute(
    val sourceId: Long
) : ScreenRoute("Source Item")

@Composable
fun SourceItemScreen(
    route: SourceItemRoute,
    navController: NavController,
    viewModel: SourceItemModel = viewModel { SourceItemModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val item = state.source
    val uriHandler = LocalUriHandler.current
    if (item == null) {
        Text("Fetching Source with id: ${route.sourceId}")
    } else {
        PropertyTable(
            name = "Source ${route.sourceId}",
            item = item,
            properties = listOf(
                PropertyRow("Url") { TextCell(it.url) },
                PropertyRow("Title") { TextCell(it.pageTitle) },
                PropertyRow("Headline") { TextCell(it.headline) },
                PropertyRow("Score") { TextCell(it.score) },
                PropertyRow("Description") { TextCell(it.description) },
                PropertyRow("Host") { TextCell(it.hostCore) },
                PropertyRow("Section") { TextCell(it.section) },
                PropertyRow("Image") { TextCell(it.image) },
                PropertyRow("Thumbnail") { TextCell(it.thumbnail) },
                PropertyRow("Seen") { DurationAgoCell(it.seenAt) },
                PropertyRow("Published") { DurationAgoCell(it.publishedAt) }
            )
        )
    }
}