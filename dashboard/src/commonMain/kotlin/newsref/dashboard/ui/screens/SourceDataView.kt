package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import newsref.dashboard.baseSpacing
import newsref.dashboard.ui.controls.ScoreChart
import newsref.dashboard.ui.table.CellControl
import newsref.dashboard.ui.table.DurationAgoCell
import newsref.dashboard.ui.table.PropertyRow
import newsref.dashboard.ui.table.PropertyTable
import newsref.dashboard.ui.table.TextCell
import newsref.dashboard.ui.table.openExternalLink
import newsref.dashboard.ui.table.textRow

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun SourceDataView(
    viewModel: SourceItemModel,
) {
    val state by viewModel.state.collectAsState()
    val source = state.source
    if (source == null) return
    val scores = state.scores
    val contents = state.contents
    val uriHandler = LocalUriHandler.current

    Column(
        verticalArrangement = Arrangement.spacedBy(baseSpacing)
    ) {
        PropertyTable(
            name = "Source ${source.id}",
            item = source,
            properties = listOf(
                textRow("Id", source.id.toString()),
                textRow("Url", source.url.href, openExternalLink(uriHandler) { it.url.href }),
                textRow("Title", source.title),
                textRow("Type", source.type?.toString()),
                PropertyRow("Score") { TextCell(it.score) },
                textRow("Host", source.url.core),
                textRow("Image", source.imageUrl),
                textRow("Thumbnail", source.thumbnail),
                textRow("embed", source.embed),
                PropertyRow("Seen") { DurationAgoCell(it.seenAt) },
                PropertyRow("Published") { DurationAgoCell(it.publishedAt) },
                PropertyRow("Accessed") { DurationAgoCell(it.accessedAt) },
                PropertyRow("ContentCount") { TextCell(it.contentCount) },
                PropertyRow("Scores") { TextCell(scores?.size ?: 0) },
                PropertyRow("Contents") { TextCell(contents?.size ?: 0) }
            )
        )

        if (scores != null && !scores.isEmpty()) {
            ScoreChart(scores)
        }
    }
}