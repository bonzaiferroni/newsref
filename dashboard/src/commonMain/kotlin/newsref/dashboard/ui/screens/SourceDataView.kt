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
            name = "Source ${source.sourceId}",
            item = source,
            properties = listOf(
                textRow("Id", source.sourceId.toString()),
                textRow("Url", source.url, openExternalLink(uriHandler) { it.url }),
                textRow("Title", source.pageTitle),
                textRow("Headline", source.headline),
                PropertyRow("Score") { TextCell(it.score) },
                textRow("Description", source.description),
                textRow("Host", source.hostCore),
                textRow("Section", source.section),
                textRow("Image", source.image),
                textRow("Thumbnail", source.thumbnail),
                PropertyRow("Seen") { DurationAgoCell(it.seenAt) },
                PropertyRow("Published") { DurationAgoCell(it.publishedAt) },
                PropertyRow("Scores") { TextCell(scores?.size ?: 0) },
                PropertyRow("Contents") { TextCell(contents?.size ?: 0) },
                PropertyRow("WordCount") { TextCell(it.wordCount) }
            )
        )

        if (scores != null && !scores.isEmpty()) {
            ScoreChart(scores)
        }
    }
}