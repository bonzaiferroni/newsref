package streetlight.web

import io.kvision.*
import io.kvision.core.*
import io.kvision.html.div
import io.kvision.panel.*
import io.kvision.routing.Routing
import io.kvision.theme.Theme
import io.kvision.theme.ThemeManager
import streetlight.web.content.aboutPage
import streetlight.web.content.homePage
import streetlight.web.nav.PageConfig
import streetlight.web.nav.portal

class App : Application() {
    init {
        require("./css/kvapp.css")
        ThemeManager.init(initialTheme = Theme.DARK, remember = false)
        Routing.init()
    }

    override fun start() {
        root("kvapp") {
            portal(
                PageConfig("Home", "/", "fas fa-home") {
                    homePage()
                },
                PageConfig("About", "/about", "fas fa-info") {
                    aboutPage()
                },
                PageConfig("Contact", "/contact", "fas fa-envelope") {
                    div("Contact page")
                }
            )
        }
//        root("kvapp") {
//            vPanel {
//                tabPanel(scrollableTabs = true) {
//                    tab("HTML", "fas fa-bars", route = "/basic") {
//                        div("Basic tab")
//                    }
//                    tab("Forms", "fas fa-edit", route = "/forms") {
//                        div("Forms tab")
//                    }
//                }
//            }
//        }
    }
}

fun main() {
    startApplication(
        ::App, module.hot,
        BootstrapModule,
        // BootstrapCssModule,
        CoreModule,
        FontAwesomeModule
    )
}