package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import compose.icons.TablerIcons
import compose.icons.tablericons.Copy
import compose.icons.tablericons.PlayerPause
import compose.icons.tablericons.PlayerPlay
import kotlinx.serialization.Serializable
import newsref.dashboard.*
import newsref.dashboard.ui.table.AlignCell
import newsref.dashboard.ui.table.DataTable
import newsref.dashboard.ui.table.DurationAgoCell
import newsref.dashboard.ui.table.EmojiCell
import newsref.dashboard.ui.table.NumberCell
import newsref.dashboard.ui.table.PropertyRow
import newsref.dashboard.ui.table.PropertyTable
import newsref.dashboard.ui.table.TableColumn
import newsref.dashboard.ui.table.TextCell
import newsref.dashboard.ui.table.addControl
import newsref.dashboard.ui.table.glowOverMin
import newsref.dashboard.ui.table.onClick
import newsref.dashboard.utils.TipType
import newsref.dashboard.utils.ToolTip
import newsref.dashboard.utils.setRawText
import newsref.model.dto.SourceInfo

@Serializable
object SourceTableRoute : ScreenRoute("Sources")

@Composable
fun SourceTableScreen(
    navController: NavController,
    viewModel: SourceTableModel = viewModel { SourceTableModel() }
) {
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val state by viewModel.state.collectAsState()
    PropertyTable(
        name = "Source Table",
        item = state,
        properties = listOf(
            PropertyRow("count") {
                val value = if (state.paused)
                    "${state.count} (+${state.count - state.countShown})" else "${state.count}"
                TextCell(value)
            }
        )
    )
    Row {
        IconButton(onClick = viewModel::togglePause) {
            val icon = when (state.paused) {
                true -> TablerIcons.PlayerPlay
                false -> TablerIcons.PlayerPause
            }
            Icon(imageVector = icon, contentDescription = "Play/Pause")
        }
        Button(onClick = { }) {
            Text("Button")
        }
    }
    DataTable(
        name = "Sources",
        rows = state.items,
        isNew = { it.sourceId > state.previousTopId},
        glowFunction = { glowOverMin(it.seenAt) },
        onFirstVisibleIndex = viewModel::trackIndex,
        columns = listOf(
            TableColumn<SourceInfo>("headline") { TextCell(it.headline ?: it.pageTitle) }
                .onClick(ToolTip("Go to source", TipType.Action)) { navController.navigate(SourceItemRoute(it.sourceId)) },
            TableColumn<SourceInfo>("url", alpha = .8f) { TextCell(it.url) }
                .onClick(ToolTip("Open in browser", TipType.Action)) { uriHandler.openUri(it.url) }
                .addControl(TablerIcons.Copy, ToolTip("Copy Url", TipType.Action)) { clipboardManager.setRawText(it.url) },
            TableColumn<SourceInfo>("Host", weight = 1f) { TextCell(it.hostName ?: it.hostCore) }
                .onClick(ToolTip("Go to host")) { /* todo: open host */ },
            TableColumn("Words", 60, AlignCell.Right) { NumberCell(it.wordCount) },
            TableColumn("seenAt", 120, AlignCell.Right, .8f) { DurationAgoCell(it.seenAt) },
            TableColumn("Ds", 30, headerTip = ToolTip("Description")) { EmojiCell("📃", it.description) },
            TableColumn("HL", 30, headerTip = ToolTip("Host Logo")) { EmojiCell("💈", it.hostLogo) },
            TableColumn("Im", 30, headerTip = ToolTip("Featured Image")) { EmojiCell("🖼", it.image) },
            TableColumn("Th", 30, headerTip = ToolTip("Thumbnail")) { EmojiCell("💅", it.thumbnail) },
        )
    )
}
