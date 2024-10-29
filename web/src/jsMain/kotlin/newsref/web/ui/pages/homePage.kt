package newsref.web.ui.pages

import io.kvision.core.Container
import io.kvision.core.Transition
import io.kvision.form.text.text
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.html.link
import io.kvision.html.p
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.bindTo
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
    col {
        p("Hello World!")
        renderStore(model.state, {it.sources}) { state ->
            for (source in state.sources) {
                val title = source.headline ?: source.url
                row {
                    div(source.score.toString())
                    button("") {
                        link(title)
                    }.onClick {
                        context.navigate(Pages.source, source.sourceId)
                    }
                }
            }
        }
    }

    return null
}