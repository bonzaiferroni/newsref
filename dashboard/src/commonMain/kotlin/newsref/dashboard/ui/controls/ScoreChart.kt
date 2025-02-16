package newsref.dashboard.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import io.github.koalaplot.core.xygraph.autoScaleYRange
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import newsref.dashboard.halfPadding
import newsref.dashboard.roundedCorners
import newsref.db.model.SourceScore
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours


@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun ScoreChart(
    scores: List<SourceScore>
) {
    if (scores.isEmpty()) {
        Text("No scores")
        return
    }

    data class ChartInfo(
        val data: List<DefaultPoint<Float, Float>>,
        val timeStart: Instant,
        val intervalCount: Int,
        val interval: Duration
    )

    val tz = TimeZone.currentSystemDefault()

    val (data, timeStart, intervalCount, interval) = remember(scores.size) {
        val timeStart = scores.first().scoredAt.toLocalDateTime(tz).date
            .atStartOfDayIn(tz)
        val timeEnd = (scores.last().scoredAt + 1.days).toLocalDateTime(tz).date
            .atStartOfDayIn(tz)
        val totalInterval = timeEnd - timeStart
        val interval = when {
            totalInterval > 2.days -> 1.days
            else -> 6.hours
        }
        val data = scores.map {
            val intervalsToPoint = ((it.scoredAt - timeStart) / interval).toFloat()
            DefaultPoint(intervalsToPoint, it.score.toFloat())
        }

        ChartInfo(data, timeStart, (totalInterval / interval).toInt(), interval)
    }

    Box(
        modifier = Modifier
            .clip(roundedCorners)
            .background(MaterialTheme.colorScheme.surfaceDim)
            .padding(halfPadding)
    ) {
        XYGraph(
            FloatLinearAxisModel(0f..intervalCount.toFloat()),
            rememberFloatLinearAxisModel(data.autoScaleYRange()),
            verticalMinorGridLineStyle = null,
            horizontalMinorGridLineStyle = null,
            xAxisLabels = {
                (timeStart + (interval * it.toInt())).toLocalDateTime(tz).toString()
            },
            modifier = Modifier.height(300.dp)
        ) {
            LinePlot(
                data,
                lineStyle = LineStyle(
                    brush = SolidColor(MaterialTheme.colorScheme.primary),
                    strokeWidth = 3.dp
                )
            )
        }
    }
}