package streetlight.web

import io.kvision.*
import io.kvision.panel.*
import io.kvision.routing.Routing
import io.kvision.theme.Theme
import io.kvision.theme.ThemeManager
import streetlight.web.content.aboutPage
import streetlight.web.content.eventPage
import streetlight.web.content.eventProfile
import streetlight.web.content.homePage
import streetlight.web.core.BasicPageBuilder
import streetlight.web.core.IdPageBuilder
import streetlight.web.core.PageConfig
import streetlight.web.core.portal

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
                PageConfig("Events", "/event", "fas fa-info", true, BasicPageBuilder {
                    eventPage()
                }),
                PageConfig("Event", "/event/:id", "fas fa-envelope", false, IdPageBuilder {
                    eventProfile(it)
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