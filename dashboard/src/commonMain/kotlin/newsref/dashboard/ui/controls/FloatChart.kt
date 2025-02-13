package newsref.dashboard.ui.controls

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.AreaBaseline
import io.github.koalaplot.core.line.AreaPlot
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberFloatLinearAxisModel
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun FloatChart(
    seriesList: ImmutableList<FloatPointSeries>,
    type: ChartType = ChartType.Line,
    xAxisMax: Float,
    yAxisMax: Float,
    xAxisLabels: (Float) -> String = { it.toString() }
) {
    ChartLayout(
        legend = {
            FlowLegend(
                modifier = Modifier.padding(16.dp).border(1.dp, MaterialTheme.colorScheme.onSurface).padding(16.dp),
                itemCount = seriesList.size,
                symbol = {
                    Symbol(
                        shape = RectangleShape,
                        fillBrush = SolidColor(cloudColors[it % cloudColors.size])
                    )
                },
                label = { Text(seriesList[it].name) }
            )
        },
        legendLocation = LegendLocation.BOTTOM
    ) {
        XYGraph(
            xAxisModel = rememberFloatLinearAxisModel(0f..xAxisMax),
            yAxisModel = rememberFloatLinearAxisModel(0f..yAxisMax),
            verticalMinorGridLineStyle = null,
            horizontalMinorGridLineStyle = null,
            xAxisLabels = xAxisLabels,
            modifier = Modifier.height(300.dp)
        ) {
            seriesList.forEachIndexed { index, series ->
                val points = series.values
                val color = cloudColors[index % cloudColors.size]
                when (type) {
                    ChartType.Line -> LinePlot(
                        points,
                        lineStyle = LineStyle(
                            brush = SolidColor(color),
                            strokeWidth = 3.dp
                        )
                    )

                    ChartType.Scatter -> LinePlot(
                        points,
                        symbol = {
                            Symbol(
                                fillBrush = SolidColor(color),
                                shape = CircleShape
                            )
                        }
                    )

                    ChartType.Area -> AreaPlot(
                        points,
                        lineStyle = LineStyle(
                            brush = SolidColor(color),
                            strokeWidth = 3.dp
                        ),
                        areaStyle = AreaStyle(
                            brush = SolidColor(color),
                            alpha = 0.5f,
                        ),
                        areaBaseline = AreaBaseline.ConstantLine(0f)
                    )
                }
            }
        }
    }
}

private val chartColors = listOf(
    Color(0xFF18B199),
    Color(0xFF004587),
    Color(0xFFA11B0A),
    Color(0xFFE3A100),
    Color(0xFF6B3E26),
    Color(0xFFDC6A00),
    Color(0xFF7D3CCF),
    Color(0xFF00B8C4),
    Color(0xFF737373),
)

enum class ChartType {
    Line,
    Area,
    Scatter,
}

data class FloatSeries(
    val name: String,
    val values: ImmutableList<Float>
)

data class FloatPointSeries(
    val name: String,
    val values: ImmutableList<FloatPoint>
)

typealias FloatPoint = DefaultPoint<Float, Float>