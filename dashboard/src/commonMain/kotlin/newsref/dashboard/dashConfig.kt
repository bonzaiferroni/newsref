package newsref.dashboard

import compose.icons.TablerIcons
import compose.icons.tablericons.Dashboard
import kotlinx.collections.immutable.persistentListOf
import newsref.app.blip.core.BlipConfig
import newsref.dashboard.ui.screens.ChapterItemScreen
import newsref.dashboard.ui.screens.ChapterTableScreen
import newsref.dashboard.ui.screens.ChartBoardScreen
import newsref.dashboard.ui.screens.FeedItemScreen
import newsref.dashboard.ui.screens.FeedTableScreen
import newsref.dashboard.ui.screens.DashHelloScreen
import newsref.dashboard.ui.screens.HostItemScreen
import newsref.dashboard.ui.screens.HostTableScreen
import newsref.dashboard.ui.screens.PageItemScreen
import newsref.dashboard.ui.screens.PageTableScreen
import newsref.dashboard.ui.screens.StartScreen
import newsref.dashboard.ui.screens.StoryItemScreen
import newsref.dashboard.ui.screens.StoryTableScreen

val dashConfig = BlipConfig(
    name = "Dashboard",
    navGraph = {
        routeScreen<StartRoute> { StartScreen(it) }
        routeScreen<PageTableRoute> { PageTableScreen(it) }
        routeScreen<PageItemRoute> { PageItemScreen(it) }
        routeScreen<FeedTableRoute> { FeedTableScreen(it) }
        routeScreen<HelloRoute> { DashHelloScreen(it) }
        routeScreen<FeedItemRoute> { FeedItemScreen(it) }
        routeScreen<ChapterTableRoute> { ChapterTableScreen(it) }
        routeScreen<ChapterItemRoute> { ChapterItemScreen(it) }
        routeScreen<HostTableRoute> { HostTableScreen(it) }
        routeScreen<HostItemRoute> { HostItemScreen(it) }
        routeScreen<ChartBoardRoute> { ChartBoardScreen(it) }
        routeScreen<StoryTableRoute> { StoryTableScreen(it) }
        routeScreen<StoryItemRoute> { StoryItemScreen(it) }
    },
    home = StartRoute(7),
    logo = TablerIcons.Dashboard,
    portalItems = persistentListOf()
)