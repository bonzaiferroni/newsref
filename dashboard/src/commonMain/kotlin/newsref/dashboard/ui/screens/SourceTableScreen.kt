package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import compose.icons.TablerIcons
import compose.icons.tablericons.Copy
import compose.icons.tablericons.PlayerPause
import compose.icons.tablericons.PlayerPlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.dashboard.*
import newsref.dashboard.ui.table.DataTable
import newsref.dashboard.ui.table.DurationAgoCell
import newsref.dashboard.ui.table.PropertyRow
import newsref.dashboard.ui.table.PropertyTable
import newsref.dashboard.ui.table.TableColumn
import newsref.dashboard.ui.table.TextCell
import newsref.dashboard.ui.table.addControl
import newsref.dashboard.ui.table.glowOverMin
import newsref.dashboard.ui.table.onClick
import newsref.dashboard.utils.setRawText
import newsref.db.services.SourceService
import newsref.model.dto.SourceInfo
import kotlin.time.Duration.Companion.minutes

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
            if (state.paused) {
                Icon(imageVector = TablerIcons.PlayerPlay, contentDescription = "Play")
            } else {
                Icon(imageVector = TablerIcons.PlayerPause, contentDescription = "Pause")
            }
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
                .onClick { navController.navigate(SourceItemRoute(it.sourceId)) },
            TableColumn<SourceInfo>("url", alpha = .8f) { TextCell(it.url) }
                .onClick { uriHandler.openUri(it.url) }
                .addControl(TablerIcons.Copy) { clipboardManager.setRawText(it.url) },
            TableColumn("seenAt", alpha = .8f) { DurationAgoCell(it.seenAt) }
        )
    )
}
