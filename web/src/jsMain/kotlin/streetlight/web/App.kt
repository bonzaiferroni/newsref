package streetlight.web

import io.kvision.*
import io.kvision.html.div
import io.kvision.panel.*
import io.kvision.routing.Routing
import io.kvision.theme.Theme
import io.kvision.theme.ThemeManager
import streetlight.web.content.aboutPage
import streetlight.web.content.eventPage
import streetlight.web.content.homePage
import streetlight.web.nav.BasicPageBuilder
import streetlight.web.nav.IdPageBuilder
import streetlight.web.nav.PageConfig
import streetlight.web.nav.portal

class App : Application() {

    init {
        require("./css/kvapp.css")
        ThemeManager.init(initialTheme = Theme.DARK, remember = false)
    }

    override fun start() {
        val routing = Routing.init()
        root("kvapp") {
            portal(
                routing,
                PageConfig("Home", "/", "fas fa-home", true, BasicPageBuilder {
                    homePage()
                }),
                PageConfig("About", "/about", "fas fa-info", true, BasicPageBuilder {
                    aboutPage()
                }),
                PageConfig("Event", "/event/:id", "fas fa-envelope", false, IdPageBuilder {
                    eventPage(it)
                })
            )
        }
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