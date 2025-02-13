package newsref.dashboard.ui.screens

import androidx.compose.runtime.Composable
import newsref.dashboard.nav.LocalNavigator
import newsref.dashboard.SourceItemRoute
import newsref.dashboard.ui.table.DataTable
import newsref.dashboard.ui.table.TableColumn
import newsref.dashboard.ui.table.TextCell
import newsref.dashboard.ui.table.columns
import newsref.dashboard.ui.table.onClick
import newsref.dashboard.utils.TipType
import newsref.dashboard.utils.ToolTip
import newsref.dashboard.utils.formatDecimals
import newsref.db.core.DistanceInfo

@Composable
fun SourceDistanceView(
    distances: List<DistanceInfo>
) {
    val nav = LocalNavigator.current
    DataTable(
        name = "Source Distances",
        items = distances,
        columnGroups = listOf(
            columns(
                TableColumn("Distance", width = 60) { TextCell(it.distance.formatDecimals()) },
                TableColumn<DistanceInfo>("Headline", weight = 1f) { TextCell(it.title) }
                    .onClick(ToolTip("Open source", TipType.Action)) { nav.go(SourceItemRoute(it.sourceId)) },
            ),
            columns(
                TableColumn("Url", weight = 1f) { TextCell(it.url.href) },
            )
        )
    )
}