package streetlight.web.nav

import io.kvision.core.*
import io.kvision.html.Div
import io.kvision.html.div
import io.kvision.html.header
import io.kvision.navbar.*
import io.kvision.routing.Routing
import io.kvision.utils.perc
import io.kvision.utils.px
import kotlinx.browser.window

fun Container.portal(
    routing: Routing,
    vararg pages: PageConfig
) {
    // add header and add nav menu
    header {
        navbar("streetlight", expand = NavbarExpand.ALWAYS) {
            nav {
                pages.forEach { page ->
                     navLink(page.name, url = page.hashRoute)
                }
            }
        }
    }

    data class PageCache(val route: String, val div: Div)
    val loaded: MutableSet<String> = mutableSetOf()
    var current: PageCache? = null
    val duration = 0.2

    // add body
    div {
        padding = 20.px
        position = Position.RELATIVE
        width = 100.perc
        height = 100.perc

        pages.forEach { page ->
            val div = div {
                transition = Transition("all", duration, "ease-in-out" )
                position = Position.ABSOLUTE
                opacity = 0.0
            }
            fun loadPage() {
                // console.log("Route ${page.route} activated")
                if (current?.route == page.route) return
                current?.div?.updateVisibility(false)
                if (!loaded.contains(page.route)) {
                    // console.log("Loading ${page.route}")
                    page.content(div)
                    loaded.add(page.route)
                }
                current = PageCache(page.route, div)
                div.updateVisibility(true)
            }
            routing.on(page.route, { loadPage() })
        }
    }
    routing.resolve()
}

fun Div.updateVisibility(visible: Boolean) {
    if (visible) {
        this.opacity = 1.0
        zIndex = 1
        paddingTop = 0.px
    } else {
        this.opacity = 0.0
        zIndex = 0
        paddingTop = 10.px
    }
}