package streetlight.web

import io.kvision.*
import io.kvision.panel.*
import io.kvision.routing.Routing
import io.kvision.theme.Theme
import io.kvision.theme.ThemeManager
import streetlight.web.pages.*
import streetlight.web.core.*
import streetlight.web.io.stores.AppModel

class App : Application() {

    init {
        require("./css/kvapp.css")
        ThemeManager.init(initialTheme = Theme.DARK, remember = false)
    }

    override fun start() {
        val context = AppContext(AppModel(), Routing.init())
        root("kvapp") {
            buildPortal(context)
                .addPages(basePages)
                .addPages(userPages)
                .build()
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