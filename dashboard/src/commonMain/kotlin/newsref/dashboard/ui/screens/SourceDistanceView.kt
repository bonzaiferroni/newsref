package newsref.dashboard.ui.screens

import androidx.compose.runtime.Composable
import newsref.dashboard.LocalNavigator
import newsref.dashboard.SourceItemRoute
import newsref.dashboard.ui.table.ColumnGroup
import newsref.dashboard.ui.table.DataTable
import newsref.dashboard.ui.table.TableColumn
import newsref.dashboard.ui.table.TextCell
import newsref.dashboard.ui.table.onClick
import newsref.dashboard.utils.TipType
import newsref.dashboard.utils.ToolTip
import newsref.dashboard.utils.twoDecimals
import newsref.db.core.DistanceInfo
import newsref.db.core.SourceDistance

@Composable
fun SourceDistanceView(
    distances: List<DistanceInfo>
) {
    val nav = LocalNavigator.current
    DataTable(
        name = "Source Distances",
        rows = distances,
        columns = listOf(
            ColumnGroup(
                TableColumn("Distance", width = 60) { TextCell(it.distance.twoDecimals()) },
                TableColumn<DistanceInfo>("Headline", weight = 1f) { TextCell(it.title) }
                    .onClick(ToolTip("Open source", TipType.Action)) { nav.go(SourceItemRoute(it.sourceId)) },
            ),
            ColumnGroup(
                TableColumn("Url", weight = 1f) { TextCell(it.url.href) },
            )
        )
    )
}