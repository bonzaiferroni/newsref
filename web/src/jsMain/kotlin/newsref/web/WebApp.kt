package newsref.web

import io.kvision.*
import io.kvision.panel.root
import io.kvision.routing.Routing
import newsref.web.core.AppContext
import newsref.web.core.Pages
import newsref.web.core.buildPortal
import newsref.web.io.stores.AppModel

class WebApp : Application() {

    init {
        require("./css/kvapp.css")
        require("./css/tw.css")
        require("./css/prose.css")
        require("./css/forms.css")
        require("./css/buttons.css")
    }

    override fun start() {
        val routing = Routing.init()
        val context = AppContext(AppModel(), routing)
        root("kvapp") {
            buildPortal(context)
                .addPages(Pages.basePages)
                .addPages(Pages.userPages)
                .build()
        }
    }
}

fun main() {
    startApplication(
        ::WebApp, module.hot,
        // BootstrapCssModule,
        CoreModule,
        FontAwesomeModule
    )
}