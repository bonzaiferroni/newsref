package streetlight.web.nav

import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.header
import io.kvision.navbar.*
import io.kvision.panel.stackPanel
import io.kvision.routing.Routing
import io.kvision.utils.px

fun Container.portal(
    vararg pages: PageConfig
) {
    // add header and add nav menu
    this.header {
        navbar("streetlight") {
            nav {
                pages.forEach { page ->
                    navLink(page.name, icon = page.icon, url = "#${page.route}")
                }
            }
        }
    }

    // add body
    this.stackPanel {
        padding = 20.px

        pages.forEach { page ->
            route(page.route) {
                val div = div()
                page.content(div)
            }
        }
    }
}

data class PageConfig(
    val name: String,
    val route: String,
    val icon: String,
    val content: Container.() -> Unit
)