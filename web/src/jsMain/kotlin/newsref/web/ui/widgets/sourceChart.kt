package newsref.web.ui.widgets

import io.kvision.chart.*
import io.kvision.core.Container
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import newsref.model.dto.SourceInfo
import newsref.web.ui.pages.ChartCache

fun Container.sourceChart(source: SourceInfo, cache: ChartCache) {
	val scores = cache.scores[source.pageId]
	val labels = cache.labels
	if (scores == null || labels == null) return

	chart(
		Configuration(
			ChartType.BAR,
			listOf(DataSets(data = scores, backgroundColor = cache.colors)),
			labels,
			options = ChartOptions(
				responsive = false,
				scales = mapOf(
					"x" to ChartScales(
						ticks = TickOptions(
							display = false
						),
						grid = GridLineOptions(
							display = true
						)
					),
					"y" to ChartScales(
						display = false,
						max = cache.maxY
					)
				),
				plugins = PluginsOptions(
					legend = LegendOptions(
						display = false
					)
				),
				elements = ElementsOptions(
					point = PointOptions(
						radius = 0
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