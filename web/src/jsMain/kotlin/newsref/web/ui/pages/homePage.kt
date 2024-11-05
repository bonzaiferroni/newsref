package newsref.web.ui.pages

import io.kvision.chart.*
import io.kvision.core.Container
import io.kvision.html.*
import kotlinx.datetime.*
import newsref.model.dto.SourceInfo
import newsref.web.core.AppContext
import newsref.web.core.Pages
import newsref.web.core.PortalEvents
import newsref.web.ui.components.renderStore
import newsref.web.ui.models.HomeModel
import newsref.web.ui.models.HomeState
import newsref.web.ui.widgets.sourceChart
import kotlin.time.Duration.Companion.days

fun Container.homePage(context: AppContext): PortalEvents? {
    val model = HomeModel()
    div(className = "flex flex-col w-full") {
        renderStore(model.state, {it.sources}) { state ->
            h4("A bird's eye view of the news this week", className = "w-full text-center")
            feedChart(state)
            for (source in state.sources) {
                feedSource(source)
            }
        }
    }

    return null
}

fun Container.feedSource(source: SourceInfo) {
    val title = source.headline ?: source.pageTitle ?: source.url
    div(className = "flex flex-row gap-4 w-full") {
        sourceChart(source)
        h3(source.score.toString(), className = "text-dim")
        link("", Pages.source.getLinkRoute(source.sourceId), className = "w-full") {
            h3(title)
        }
        val image = source.thumbnail ?: source.hostLogo
        image?.let {
            image(it, className = "w-16 object-contain")
        }
    }
}

fun Container.feedChart(state: HomeState) {
    val now = Clock.System.now()
    val dayCount = state.timeSpan.inWholeDays.toInt()
    val scores = mutableListOf<Int>()
    val days = mutableListOf<String>()
    for (i in 1..dayCount) {
        val date = (now - (dayCount - i).days).toLocalDateTime(TimeZone.currentSystemDefault()).date
        val score = state.sources.sumOf { source ->
            val time = source.publishedAt?.takeIf { it >= (now - state.timeSpan) }
                ?: source.scores.first().scoredAt
            if (time.toLocalDateTime(TimeZone.currentSystemDefault()).date == date) source.score else 0
        }
        scores.add(score)
        days.add(date.dayOfWeek.toString())
    }
    chart(
        Configuration(
            ChartType.BAR,
            listOf(DataSets(data = scores)),
            days,
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