package newsref.app

import compose.icons.TablerIcons
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.News
import compose.icons.tablericons.YinYang
import pondui.ui.core.PondConfig
import pondui.ui.nav.defaultScreen
import kotlinx.collections.immutable.persistentListOf
import newsref.app.ui.*
import pondui.ui.core.RouteConfig
import pondui.ui.nav.PortalDoor

val appConfig = PondConfig(
    name = "Newsref",
    logo = TablerIcons.News,
    home = StartRoute,
    doors = persistentListOf(
        PortalDoor(TablerIcons.CalendarEvent, ChapterFeedRoute()),
        PortalDoor(TablerIcons.News, HostFeedRoute),
        PortalDoor(TablerIcons.YinYang, HelloRoute),
    ),
    routes = persistentListOf(
        RouteConfig(StartRoute::matchRoute) { defaultScreen<StartRoute> { StartScreen(it) } },
        RouteConfig(HelloRoute::matchRoute) { defaultScreen<HelloRoute> { HelloScreen(it) } },
        RouteConfig() { defaultScreen<ChapterFeedRoute> { ChapterFeedScreen(it) } },
        RouteConfig() { defaultScreen<ChapterRoute> { ChapterScreen(it) } },
        RouteConfig() { defaultScreen<ChapterPageRoute> { ChapterPageScreen(it) } },
        RouteConfig() { defaultScreen<HostFeedRoute> { HostFeedScreen(it) } },
        RouteConfig() { defaultScreen<HostRoute> { HostScreen(it) } },
        RouteConfig() { defaultScreen<PageRoute> { PageScreen(it) } },
    )
)