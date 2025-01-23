package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import newsref.dashboard.ui.table.BooleanCell
import newsref.dashboard.ui.table.ColumnGroup
import newsref.dashboard.ui.table.DataTable
import newsref.dashboard.ui.table.TableColumn
import newsref.dashboard.ui.table.TextCell
import newsref.dashboard.ui.table.addControl
import newsref.dashboard.ui.table.openExternalLink
import newsref.model.dto.LinkInfo

@Composable
fun LinkInfoView(
    name: String,
    links: List<LinkInfo>,
) {
    val uriHandler = LocalUriHandler.current

    DataTable(
        name = name,
        rows = links,
        columns = listOf(
            ColumnGroup(
                TableColumn<LinkInfo>("Url", weight = 1f) { TextCell(it.url) }
                    .addControl(openExternalLink(uriHandler) { it.url })
            ),
            ColumnGroup(
                TableColumn("Text", weight = 1f) { TextCell(it.urlText) },
                // TableColumn("Ext", width = 40) { BooleanCell(it.) },
            )
        )
    )
}