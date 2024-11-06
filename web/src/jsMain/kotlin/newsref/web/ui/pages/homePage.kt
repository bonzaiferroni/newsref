package newsref.web.ui.pages

import io.kvision.chart.*
import io.kvision.core.Container
import kotlinx.datetime.*
import newsref.model.core.NewsSpan
import newsref.model.dto.SourceInfo
import newsref.web.core.AppContext
import newsref.web.core.Pages
import newsref.web.core.PortalEvents
import newsref.web.ui.components.iconLabel
import newsref.web.ui.components.menu
import newsref.web.ui.components.renderStore
import newsref.web.ui.models.HomeModel
import newsref.web.ui.models.HomeState
import newsref.web.ui.css.div
import newsref.web.ui.css.*
import newsref.web.ui.widgets.sourceChart

fun Container.homePage(context: AppContext): PortalEvents? {
	val model = HomeModel()
	div(col + w_full) {
		renderStore(model.state, { it.refreshed }) { state ->
			div(row + w_full + gap_4 + justify_center) {
				h4("A bird's eye view of the news over the last:")
				menu(
					value = state.newsSpan,
					options = listOf(NewsSpan.DAY, NewsSpan.WEEK, NewsSpan.MONTH, NewsSpan.YEAR),
					bind = model::changeSpan
				)
			}
			feedChart(state)
			for (source in state.sources) {
				feedSource(source)
			}
		}
	}

	return null
}

fun Container.feedSource(source: SourceInfo) {
	val title = source.headline?.takeIf { it.isNotEmpty() }
		?: source.pageTitle?.takeIf { it.isNotEmpty()} ?: source.url
	div(row + gap_4 + w_full + items_center) {
		sourceChart(source)
		iconLabel(fa_link + text_muter, col) {
			h3(source.score.toString(), text_center + text_muted)
		}
		div(col + flex_grow) {
			link(Pages.source.getLinkRoute(source.sourceId), w_full) {
				h3(title)
			}
			h3(source.hostCore, text_muted)
		}
		val image = source.thumbnail ?: source.hostLogo
		image?.let {
			image(it, w_16 + object_contain)
		}
	}
}

fun Container.feedChart(state: HomeState) {
	val now = Clock.System.now()
	val duration = state.newsSpan.duration
	val bucketCount = when (state.newsSpan) {
		NewsSpan.DAY -> 12
		NewsSpan.WEEK -> 7
		NewsSpan.MONTH -> 30
		NewsSpan.YEAR -> 12
	}
	val bucketSpan = duration / bucketCount
	val buckets = mutableListOf<Int>()
	val startTimes = mutableListOf<Instant>()
	for (i in 1..bucketCount) {
		val start = now - bucketSpan * (bucketCount - i)
		buckets.add(0)
		startTimes.add(start)
	}
	for (source in state.sources) {
		var bucket = 0
		var lastScore = 0
		var currentScore = 0
		val firstScore = source.scores.first()
		for (i in 0 until bucketCount) {
			if (startTimes[i] > firstScore.scoredAt) break
			bucket++
		}
		for (score in source.scores) {
			if (bucket + 1 == bucketCount) break
			if (score.scoredAt < startTimes[bucket + 1]) {
				currentScore = score.score
				continue
			}
			buckets[bucket] += currentScore - lastScore
			lastScore = currentScore
			currentScore = score.score
			bucket++
		}
		val finalScore = source.scores.last().score
		buckets[bucket] += finalScore - lastScore
	}

	val labels = when (state.newsSpan) {
		NewsSpan.DAY -> startTimes.map {
			val hour = it.toLocalDateTime(TimeZone.currentSystemDefault()).hour
			"$hour:00"
		}
		NewsSpan.WEEK -> startTimes.map {
			it.toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.toString()
		}
		NewsSpan.MONTH -> startTimes.map {
			it.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
		}
		NewsSpan.YEAR -> startTimes.map {
			it.toLocalDateTime(TimeZone.currentSystemDefault()).month.toString()
		}
	}

	chart(
		Configuration(
			ChartType.BAR,
			listOf(DataSets(data = buckets)),
			labels,
			ChartOptions(
				maintainAspectRatio = true,
				plugins = PluginsOptions(
					legend = LegendOptions(
						display = false
					)
				)
			)
		),
		className = "w-full"
	)
}