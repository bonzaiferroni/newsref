package streetlight.web

import io.kvision.*
import io.kvision.html.*
import io.kvision.navbar.navbar
import io.kvision.panel.dockPanel
import io.kvision.panel.hPanel
import io.kvision.panel.root
import io.kvision.panel.vPanel
import io.kvision.table.TableType
import io.kvision.table.cell
import io.kvision.table.row
import io.kvision.table.table
import io.kvision.theme.Theme
import io.kvision.theme.ThemeManager
import io.kvision.utils.px

class App : Application() {
    init {
        require("./css/kvapp.css")
        ThemeManager.init(initialTheme = Theme.DARK, remember = false)
    }

    override fun start() {
        root("kvapp") {
            navbar("NavBar") {
                nav {
                    link("Home", "./", icon = "fas fa-home")
                    link("About", "./", icon = "fas fa-info")
                    link("Contact", "./", icon = "fas fa-envelope")
                }
            }
            penguin()
        }
    }
}

fun main() {
    startApplication(
        ::App, module.hot,
        BootstrapModule,
        BootstrapCssModule,
        CoreModule,
        FontAwesomeModule
    )
}
