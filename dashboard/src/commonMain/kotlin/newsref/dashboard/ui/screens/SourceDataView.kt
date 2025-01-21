package newsref.dashboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.autoScaleXRange
import io.github.koalaplot.core.xygraph.autoScaleYRange
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import newsref.dashboard.baseSpacing
import newsref.dashboard.halfPadding
import newsref.dashboard.roundedCorners
import newsref.dashboard.ui.controls.ScoreChart
import newsref.dashboard.ui.table.DurationAgoCell
import newsref.dashboard.ui.table.PropertyRow
import newsref.dashboard.ui.table.PropertyTable
import newsref.dashboard.ui.table.TextCell
import newsref.model.data.SourceScore
import newsref.model.dto.SourceInfo
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun SourceDataView(
    sourceInfo: SourceInfo,
    scores: List<SourceScore>?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(baseSpacing)
    ) {
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
                PropertyRow("Published") { DurationAgoCell(it.publishedAt) },
                PropertyRow("Scores") { TextCell(scores?.size ?: 0)}
            )
        )

        if (scores != null && !scores.isEmpty()) {
            ScoreChart(scores)
        }
    }
}
