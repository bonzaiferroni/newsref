package newsref.web.ui.widgets

import io.kvision.chart.*
import io.kvision.core.Container
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import newsref.model.dto.SourceInfo

fun Container.sourceChart(source: SourceInfo) {
	chart(
		Configuration(
			ChartType.LINE,
			listOf(DataSets(data = source.scores.map {
				it.score
			})),
			source.scores.map { it.scoredAt.toFormattedString() },
			options = ChartOptions(
				responsive = false,
				scales = mapOf(
					"x" to ChartScales(
						display = false
					),
					"y" to ChartScales(
						display = false
					)
				),
				plugins = PluginsOptions(
					legend = LegendOptions(
						display = false
					)
				)
			)
		), 64, 64
	)
}

data class ChartPoint(
	val x: Int,
	val y: String,
)

fun Instant.toFormattedString() = this.toLocalDateTime(TimeZone.currentSystemDefault()).let {
	"${it.date}T${it.time.hour}:${it.time.minute}:${it.time.second}"
}