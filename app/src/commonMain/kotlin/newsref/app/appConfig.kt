package newsref.app

import compose.icons.TablerIcons
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.News
import compose.icons.tablericons.YinYang
import kotlinx.collections.immutable.persistentListOf
import newsref.app.blip.core.BlipConfig
import newsref.app.blip.nav.PortalRoute
import newsref.app.blip.nav.defaultScreen
import newsref.app.ui.*

val appConfig = BlipConfig(
    name = "Newsref",
    logo = TablerIcons.News,
    home = StartRoute,
    navGraph = {
        defaultScreen<StartRoute> { StartScreen(it) }
        defaultScreen<HelloRoute> { HelloScreen(it) }
        defaultScreen<ChapterFeedRoute> { ChapterFeedScreen(it) }
        defaultScreen<ChapterRoute> { ChapterScreen(it) }
        defaultScreen<ChapterPageRoute> { ChapterPageScreen(it) }
        defaultScreen<HostFeedRoute> { HostFeedScreen(it) }
        defaultScreen<HostRoute> { HostScreen(it) }
        defaultScreen<PageRoute> { PageScreen(it) }
    },
    portalItems = persistentListOf(
        PortalRoute(TablerIcons.YinYang, "Hello", HelloRoute),
        PortalRoute(TablerIcons.CalendarEvent, "Chapter Feed", ChapterFeedRoute()),
        PortalRoute(TablerIcons.News, "Hosts", HostFeedRoute)
    )
)