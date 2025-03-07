package newsref.app

import compose.icons.TablerIcons
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.News
import compose.icons.tablericons.YinYang
import kotlinx.collections.immutable.persistentListOf
import newsref.app.blip.core.BlipConfig
import newsref.app.blip.nav.PortalRoute
import newsref.app.blip.nav.routeScreen
import newsref.app.ui.*

val appConfig = BlipConfig(
    name = "Newsref",
    logo = TablerIcons.News,
    home = StartRoute,
    navGraph = {
        routeScreen<StartRoute> { StartScreen(it) }
        routeScreen<HelloRoute> { HelloScreen(it) }
        routeScreen<ChapterFeedRoute> { ChapterFeedScreen(it) }
        routeScreen<ChapterRoute> { ChapterScreen(it) }
        routeScreen<ChapterSourceRoute> { ChapterSourceScreen(it) }
        routeScreen<HostFeedRoute> { HostFeedScreen(it) }
        routeScreen<HostRoute> { HostScreen(it) }
    },
    portalItems = persistentListOf(
        PortalRoute(TablerIcons.YinYang, "Hello", HelloRoute),
        PortalRoute(TablerIcons.CalendarEvent, "Chapter Feed", ChapterFeedRoute()),
        PortalRoute(TablerIcons.News, "Hosts", HostFeedRoute)
    )
)