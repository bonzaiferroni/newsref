package streetlight.web.core

import io.kvision.core.*
import io.kvision.html.*
import io.kvision.panel.hPanel
import io.kvision.routing.Routing
import io.kvision.utils.perc
import io.kvision.utils.plus
import io.kvision.utils.px
import kotlinx.browser.window
import streetlight.web.Layout
import streetlight.web.gap
import streetlight.web.getIdFromUrl

fun Container.portal(
    routing: Routing,
    vararg pages: PageConfig
) {
    // add header and add nav menu
    header {
        nav(className = "navbar ${BsBgColor.BODYTERTIARY.className}") {
            paddingTop = Layout.halfPad
            paddingBottom = Layout.halfPad
            paddingLeft = Layout.defaultPad
            paddingRight = Layout.halfPad
            hPanel(alignItems = AlignItems.CENTER) {
                gap = Layout.defaultGap
                image("img/logo-small.png", className = "glow-effect-color") {
                    width = 30.px
                }
                link(label = "", url = "#/") {
                    h2 {
                        color = Color("var(--bs-navbar-brand-color)")
                        span("streetl")
                        span("i", className = "glow-effect") {
                            id = "logo-i"
                            color = Color("BurlyWood")
                        }
                        span("ght")
                    }
                }
            }

            hPanel(justify = JustifyContent.FLEXEND) {
                pages.forEach { page ->
                    if (!page.navLink) return@forEach
                    link(className = "navbar-link", label = page.name, url = page.linkRoute) {
                        padding = Layout.halfPad
                    }
                }
            }
        }
    }

    data class PageCache(val route: String, val div: Div)

    val loaded: MutableSet<String> = mutableSetOf()
    var current: PageCache? = null
    var events: PortalEvents? = null
    val duration = 0.3

    // add body
    div() {
        position = Position.RELATIVE
        width = 100.perc
        height = 100.perc

        pages.forEach { page ->
            val div = div(className = "content ${page.name}") {
                padding = Layout.defaultPad
                transition = Transition("all", duration, "ease-out")
                position = Position.ABSOLUTE
                width = 100.perc
                left = 0.px
                right = 0.px
            }

            fun loadPage() {
                console.log("Portal.loadPage ${page.route}")
                events?.let { it.onUnload?.invoke() }

                when (page.builder) {
                    is CachedPageBuilder -> {
                        if (current?.route == page.route) return
                        current?.div?.updateVisibility(false)
                        if (!loaded.contains(page.route)) {
                            // console.log("Loading ${page.route}")
                            events = page.builder.content(div)
                            loaded.add(page.route)
                        }
                    }

                    is IdPageBuilder -> {
                        current?.div?.updateVisibility(false)
                        // console.log("Loading ${page.route}")
                        div.removeAll()
                        val id = window.location.href.getIdFromUrl()
                        if (id == null) {
                            div.add(P("Portal.loadPage: missing id"))
                        } else {
                            events = page.builder.content(div, id)
                        }
                    }

                    is TransientPageBuilder -> {
                        current?.div?.updateVisibility(false)
                        // console.log("Loading ${page.route}")
                        div.removeAll()
                        events = page.builder.content(div)
                    }
                }
                events?.onLoad?.invoke()
                current = PageCache(page.route, div)
                div.updateVisibility(true)
            }
            div.updateVisibility(false)
            routing.on(page.route, { loadPage() })
            if (routing.current?.firstOrNull()?.route?.name == page.route) {
                loadPage()
            }
        }
    }
    routing.resolve()
}

fun Div.updateVisibility(visible: Boolean) {
    if (visible) {
        this.opacity = 1.0
        zIndex = 1
        paddingTop = Layout.defaultPad
    } else {
        this.opacity = 0.0
        zIndex = 0
        paddingTop = Layout.defaultPad + 10
    }
}