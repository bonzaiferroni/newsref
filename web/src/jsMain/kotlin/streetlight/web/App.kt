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
                .addPages(
                    PageConfig("Home", "/", "fas fa-home", false, CachedPageBuilder {
                        homePage()
                    }),
                    PageConfig("About", "/about", "fas fa-info", true, CachedPageBuilder {
                        aboutPage()
                    }),
                    PageConfig("Events", "/event", "fas fa-info", true, CachedPageBuilder {
                        eventPage()
                    }),
                    PageConfig("Event", "/event/:id", "fas fa-envelope", false, IdPageBuilder {
                        eventProfile(it)
                    }),
                    PageConfig("User", "/user", "fas fa-home", true, TransientPageBuilder {
                        userPage(context)
                    }),
                    PageConfig("Login", "/login", "fas fa-home", false, CachedPageBuilder {
                        loginPage(context)
                    }),
                    PageConfig("EditUser", "/user/edit", "fas fa-home", false, TransientPageBuilder {
                        editUserPage(context)
                    }),
                    PageConfig("Admin", "/admin", "fas fa-home", false, TransientPageBuilder {
                        adminPage(context)
                    })
                )
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