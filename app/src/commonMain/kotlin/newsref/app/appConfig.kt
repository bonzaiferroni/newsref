package newsref.app

import compose.icons.TablerIcons
import compose.icons.tablericons.CalendarEvent
import compose.icons.tablericons.News
import compose.icons.tablericons.YinYang
import pondui.ui.core.PondConfig
import pondui.ui.nav.PortalRoute
import pondui.ui.nav.defaultScreen
import kotlinx.collections.immutable.persistentListOf
import newsref.app.ui.*

val appConfig = PondConfig(
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
        PortalRoute(TablerIcons.CalendarEvent, ChapterFeedRoute()),
        PortalRoute(TablerIcons.News, HostFeedRoute),
        PortalRoute(TablerIcons.YinYang, HelloRoute),
    )
)