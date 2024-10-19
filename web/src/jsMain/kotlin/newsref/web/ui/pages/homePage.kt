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
import newsref.web.core.PortalEvents
import newsref.web.ui.components.col
import newsref.web.ui.components.renderStore
import newsref.web.ui.components.row
import newsref.web.ui.models.HomeModel

fun Container.homePage(): PortalEvents? {
    val model = HomeModel()
    col {
        p("Hello World!")
        renderStore(model.state, {it.sources}) { state ->
            for (source in state.sources) {
                val title = source.article?.headline ?: source.leadTitle ?: source.url
                row {
                    div(source.citationCount.toString())
                    link(title, source.url)
                }
            }
        }
    }

    return null
}