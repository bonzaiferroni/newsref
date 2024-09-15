package streetlight.web.core

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.html.*
import io.kvision.panel.hPanel
import io.kvision.state.ObservableValue
import streetlight.web.Css
import streetlight.web.Layout
import streetlight.web.gap

fun Container.portalBar(
    pages: List<PageConfig>,
    onPageLoad: ObservableValue<PageConfig>
) {
    header {
        nav(className = Css.navbar) {
            hPanel(alignItems = AlignItems.CENTER) {
                gap = Layout.halfGap
                image("img/logo-small.png", className = Css.navbar_logo)
                link(label = "", url = "#/") {
                    h2 {
                        span("Streetl")
                        span("i", className = Css.navbar_logo_i)
                        span("ght")
                    }
                }
            }

            hPanel(justify = JustifyContent.FLEXEND) {
                pages.forEach { page ->
                    if (!page.navLink) return@forEach
                    val link = link(className = Css.navbar_link, label = page.name, url = page.linkRoute) {
                        padding = Layout.halfPad
                    }
                    onPageLoad.subscribe {
                        if (it.route.contains(page.route)) {
                            link.addCssClass(Css.navbar_link_active)
                        } else {
                            link.removeCssClass(Css.navbar_link_active)
                        }
                    }
                }
            }
        }
    }
}