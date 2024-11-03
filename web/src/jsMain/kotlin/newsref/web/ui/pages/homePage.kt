package newsref.web.ui.pages

import io.kvision.core.Container
import io.kvision.core.Transition
import io.kvision.form.text.text
import io.kvision.html.*
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.bindTo
import newsref.model.dto.SourceInfo
import newsref.web.core.AppContext
import newsref.web.core.Pages
import newsref.web.core.PortalEvents
import newsref.web.core.navigate
import newsref.web.ui.components.col
import newsref.web.ui.components.renderStore
import newsref.web.ui.components.row
import newsref.web.ui.models.HomeModel

fun Container.homePage(context: AppContext): PortalEvents? {
    val model = HomeModel()
    div(className = "flex flex-col w-full") {
        renderStore(model.state, {it.sources}) { state ->
            for (source in state.sources) {
                feedSource(source)
            }
        }
    }

    return null
}

fun Container.feedSource(source: SourceInfo) {
    val title = source.headline ?: source.url
    div(className = "flex flex-row gap-4 w-full") {
        h3(source.score.toString(), className = "text-dim")
        link("", Pages.source.getLinkRoute(source.sourceId), className = "w-full") {
            h3(title)
        }
        val image = source.thumbnail ?: source.hostLogo
        image?.let {
            image(it, className = "w-16 h-auto")
        }
    }
}