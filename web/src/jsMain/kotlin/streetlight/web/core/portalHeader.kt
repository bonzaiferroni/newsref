package streetlight.web.core

import io.kvision.core.*
import io.kvision.html.*
import io.kvision.panel.hPanel
import io.kvision.state.ObservableValue
import io.kvision.utils.px
import streetlight.web.Layout
import streetlight.web.gap

fun Container.portalHeader(
    pages: List<PageConfig>,
    onPageLoad: ObservableValue<PageConfig>
) {
    header {
        nav(className = "navbar ${BsBgColor.BODYTERTIARY.className}") {
            paddingTop = Layout.halfPad
            paddingBottom = Layout.halfPad
            paddingLeft = Layout.defaultPad
            paddingRight = Layout.halfPad
            hPanel(alignItems = AlignItems.CENTER) {
                gap = Layout.halfGap
                image("img/logo-small.png", className = "glow-effect-color") {
                    width = 30.px
                }
                link(label = "", url = "#/") {
                    h2 {
                        color = Color("var(--bs-navbar-brand-color)")
                        span("Streetl")
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
                    val link = link(className = "navbar-link", label = page.name, url = page.linkRoute) {
                        padding = Layout.halfPad
                    }
                    onPageLoad.subscribe {
                        if (it.route.contains(page.route)) {
                            link.addCssClass("active-page")
                        } else {
                            link.removeCssClass("active-page")
                        }
                    }
                }
            }
        }
    }
}