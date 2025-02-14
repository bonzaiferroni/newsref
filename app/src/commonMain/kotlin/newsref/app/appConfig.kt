package newsref.app

import newsref.app.blip.core.BlipConfig
import newsref.app.blip.nav.routeScreen
import newsref.app.ui.HelloScreen
import newsref.app.ui.StartScreen

val appConfig = BlipConfig(
    navGraph = {
        routeScreen<StartRoute> { StartScreen(it) }
        routeScreen<HelloRoute> { HelloScreen(it) }
    }
)