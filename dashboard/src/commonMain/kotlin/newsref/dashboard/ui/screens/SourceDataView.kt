package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import newsref.dashboard.baseSpacing
import newsref.dashboard.ui.controls.TimeChart
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
    val contents = state.contents

    Column(
        verticalArrangement = Arrangement.spacedBy(baseSpacing)
    ) {
        PropertyTable(
            name = "Source ${source.id}",
            item = source,
            properties = listOf(
                textRow("Id", source.id.toString()),
                textRow("Url", source.url.href, 1, openExternalLink { it.url.href }),
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
                PropertyRow("Scores") { TextCell(state.chartData?.times?.size ?: 0) },
                PropertyRow("Contents") { TextCell(contents.size) },
                PropertyRow("Outbound links") { TextCell(state.outbound.size) },
                PropertyRow("Inbound links") { TextCell(state.inbound.size) },
                PropertyRow("Distances") { TextCell(state.distances.size) }
            )
        )

        state.chartData?.let { TimeChart(it) }

        val article = state.article
        if (article != null) {
            PropertyTable(
                name = "Article",
                item = article,
                properties = listOf(
                    textRow("Id", article.id.toString()),
                    textRow("Headline", article.headline),
                    textRow("Alternative Headline", article.alternativeHeadline),
                    textRow("Description", article.description),
                    textRow("Cannon Url", article.cannonUrl),
                    textRow("Section", article.section),
                    textRow("Keywords", article.keywords?.joinToString(", ")),
                    textRow("Word Count", article.wordCount.toString()),
                    textRow("Is Free", article.isFree.toString()),
                    textRow("Language", article.language),
                    textRow("Comment Count", article.commentCount.toString()),
                    PropertyRow("Modified At") { DurationAgoCell(it.modifiedAt) },
                )
            )
        }
    }
}