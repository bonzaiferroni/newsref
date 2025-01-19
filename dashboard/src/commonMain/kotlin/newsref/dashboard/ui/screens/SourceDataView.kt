package newsref.dashboard.ui.screens

import androidx.compose.runtime.Composable
import newsref.dashboard.ui.table.DurationAgoCell
import newsref.dashboard.ui.table.PropertyRow
import newsref.dashboard.ui.table.PropertyTable
import newsref.dashboard.ui.table.TextCell
import newsref.model.dto.SourceInfo

@Composable
fun SourceDataView(sourceInfo: SourceInfo) {
    PropertyTable(
        name = "Source ${sourceInfo.sourceId}",
        item = sourceInfo,
        properties = listOf(
            PropertyRow("Id", { sourceInfo.sourceId.toString() }) { TextCell(it.sourceId.toString()) },
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