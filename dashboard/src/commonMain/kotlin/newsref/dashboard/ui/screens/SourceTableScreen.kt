package newsref.dashboard.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import newsref.dashboard.*
import newsref.dashboard.ui.table.DataTable
import newsref.dashboard.ui.table.PropertyRow
import newsref.dashboard.ui.table.PropertyTable
import newsref.dashboard.ui.table.TableColumn
import newsref.dashboard.ui.table.TextCell
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
    val state by viewModel.state.collectAsState()
    PropertyTable(
        name = "Source Table",
        item = state,
        properties = listOf(
            PropertyRow("count") { TextCell(it.count.toInt()) }
        )
    )
    DataTable(
        name = "Sources",
        rows = state.items,
        columns = listOf(
            TableColumn("url") { TextCell(it.url) }
        )
    )
}
