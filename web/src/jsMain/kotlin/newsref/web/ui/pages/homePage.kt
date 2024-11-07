package newsref.web.ui.pages

import io.kvision.chart.*
import io.kvision.core.Container
import io.kvision.html.h3
import kotlinx.browser.window
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
import newsref.web.utils.format
import newsref.web.utils.pluralize
import org.w3c.dom.SMOOTH
import org.w3c.dom.ScrollBehavior
import org.w3c.dom.ScrollToOptions
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

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
			val cache = ChartCache()
			feedChart(state, cache)
			for (source in state.sources) {
				feedSource(source, cache)
			}
		}
	}

	return null
}

class ChartCache {
	val scores = mutableMapOf<Long, List<Int>>()
	var labels: List<String>? = null
	var maxY: Int = 0
}

fun Container.feedSource(source: SourceInfo, cache: ChartCache) {
	val title = source.headline?.takeIf { it.isNotEmpty() }
		?: source.pageTitle?.takeIf { it.isNotEmpty()} ?: source.url
	val now = Clock.System.now()
	val timeSince = (now - (source.publishedAt ?: source.seenAt))
	val label = if (timeSince < 1.hours) {
		val minutes = timeSince.inWholeMinutes
		"$minutes minutes${minutes.pluralize()}"
	} else if (timeSince < 24.hours) {
		val hours = timeSince.inWholeHours
		"$hours hour${hours.pluralize()}"
	} else if (timeSince < 800.days) {
		val days = timeSince.inWholeDays
		"$days day${days.pluralize()}"
	} else {
		"${(timeSince.inWholeDays / 365.2422).format()} years"
	}
	div(row + gap_4 + w_full + items_start) {
		div(col + gap_2 + "md" * flex_row) {
			sourceChart(source, cache)
			iconLabel(fa_link + text_muter, row + items_center + gap_1 + justify_center + "md" * flex_col) {
				h3(source.score.toString(), text_center + text_muted)
			}
		}
		div(col + flex_grow) {
			link(Pages.source.getLinkRoute(source.sourceId), clearfix) {
				val image = source.thumbnail ?: source.hostLogo
				image?.let {
					image(it, w_16 + object_contain + float_right + ml_2 + mb_2)
				}
				h3(title, inline)
			}.onClick {
				window.scrollTo(options = ScrollToOptions(
					top = 100.0,
					behavior = ScrollBehavior.SMOOTH
				))
			}
			div(row + gap_4) {
				h3(source.hostCore, text_muted)
				h3("from $label ago", text_muted)
			}
		}
	}
}

fun Container.feedChart(state: HomeState, cache: ChartCache) {
	val now = Clock.System.now()
	// val nowLocal = now.toLocalDateTime(TimeZone.currentSystemDefault())
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
		val sourceBuckets = (0 until bucketCount).map { 0 }.toMutableList()
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
			sourceBuckets[bucket] = currentScore
			cache.maxY = maxOf(currentScore, cache.maxY)
			lastScore = currentScore
			currentScore = score.score
			bucket++
		}
		val finalScore = source.scores.last().score
		buckets[bucket] += finalScore - lastScore
		cache.maxY = maxOf(finalScore, cache.maxY)
		sourceBuckets[bucket] = finalScore
		cache.scores[source.sourceId] = sourceBuckets
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

	cache.labels = labels

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
		className = w_full.toString()
	)
}