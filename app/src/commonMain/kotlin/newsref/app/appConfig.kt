package newsref.app

import compose.icons.TablerIcons
import compose.icons.tablericons.News
import compose.icons.tablericons.YinYang
import kotlinx.collections.immutable.persistentListOf
import newsref.app.blip.core.BlipConfig
import newsref.app.blip.nav.PortalAction
import newsref.app.blip.nav.routeScreen
import newsref.app.ui.*

val appConfig = BlipConfig(
    logo = TablerIcons.News,
    home = StartRoute,
    navGraph = {
        routeScreen<StartRoute> { StartScreen(it) }
        routeScreen<HelloRoute> { HelloScreen(it) }
    },
    portalActions = persistentListOf(
        PortalAction(TablerIcons.YinYang, "Hello") { it.go(HelloRoute) }
    )
)