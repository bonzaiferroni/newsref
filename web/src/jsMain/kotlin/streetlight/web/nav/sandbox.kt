package streetlight.web.nav

import io.kvision.core.Container
import io.kvision.core.onClick
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.panel.hPanel
import io.kvision.panel.stackPanel
import io.kvision.routing.Routing
import io.kvision.utils.px

fun Container.sandbox(
    routing: Routing,
    vararg pages: PageConfig
) {
    hPanel(spacing = 10) {
        padding = 10.px
        pages.forEach { page ->
            button(page.name) {
                onClick {
                    routing.navigate(page.route)
                }
            }
        }
    }

    stackPanel {
        pages.forEach { page ->
//            route(page.route) {
//                val div = div()
//                page.content(div)
//            }
            routing.on(page.route, {console.log(page.name)})
        }
    }
    routing.resolve()
    routing.routes.forEach { route ->
        console.log(route.name)
    }
}