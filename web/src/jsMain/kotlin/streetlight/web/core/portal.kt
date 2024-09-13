package streetlight.web.core

import io.kvision.core.Container
import io.kvision.core.Position
import io.kvision.core.Transition
import io.kvision.core.Widget
import io.kvision.html.P
import io.kvision.html.div
import io.kvision.state.ObservableValue
import io.kvision.utils.perc
import io.kvision.utils.plus
import io.kvision.utils.px
import kotlinx.browser.window
import streetlight.web.Layout
import streetlight.web.getIdFromUrl
import streetlight.web.ui.components.col

fun Container.portal(
    context: AppContext,
    pages: List<PageConfig>
) {
    val (model, routing) = context
    // add header and add nav menu
    val onPageLoad = ObservableValue(Pages.home)

    portalHeader(pages, onPageLoad)

    data class PageCache(val route: String, val widget: Widget)

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
            val div = col(className = "content ${page.name} p-3") {
                transition = Transition("all", duration, "ease-out")
                position = Position.ABSOLUTE
                width = 100.perc
                left = 0.px
                right = 0.px
            }

            fun loadPage() {
                console.log("Portal.loadPage: loading ${page.route}")
                events?.let { it.onUnload?.invoke() }

                when (page.builder) {
                    is CachedPageBuilder -> {
                        if (current?.route == page.route) return
                        current?.widget?.updateVisibility(false)
                        if (!loaded.contains(page.route)) {
                            // console.log("Loading ${page.route}")
                            events = page.builder.content(div, context)
                            loaded.add(page.route)
                        }
                    }

                    is IdPageBuilder -> {
                        current?.widget?.updateVisibility(false)
                        // console.log("Loading ${page.route}")
                        div.removeAll()
                        val id = window.location.href.getIdFromUrl()
                        if (id == null) {
                            div.add(P("Portal.loadPage: missing id"))
                        } else {
                            events = page.builder.content(div, context, id)
                        }
                    }

                    is TransientPageBuilder -> {
                        current?.widget?.updateVisibility(false)
                        // console.log("Loading ${page.route}")
                        div.removeAll()
                        events = page.builder.content(div, context)
                    }
                }
                events?.onLoad?.invoke()
                current = PageCache(page.route, div)
                div.updateVisibility(true)
                onPageLoad.value = page
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

fun Widget.updateVisibility(visible: Boolean) {
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