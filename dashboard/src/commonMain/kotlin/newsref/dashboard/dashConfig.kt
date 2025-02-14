package newsref.dashboard

import newsref.app.blip.core.BlipConfig
import newsref.dashboard.ui.screens.ChapterItemScreen
import newsref.dashboard.ui.screens.ChapterTableScreen
import newsref.dashboard.ui.screens.ChartBoardScreen
import newsref.dashboard.ui.screens.FeedItemScreen
import newsref.dashboard.ui.screens.FeedTableScreen
import newsref.dashboard.ui.screens.HelloScreen
import newsref.dashboard.ui.screens.HostItemScreen
import newsref.dashboard.ui.screens.HostTableScreen
import newsref.dashboard.ui.screens.SourceItemScreen
import newsref.dashboard.ui.screens.SourceTableScreen
import newsref.dashboard.ui.screens.StartScreen
import newsref.dashboard.ui.screens.StoryItemScreen
import newsref.dashboard.ui.screens.StoryTableScreen

val dashConfig = BlipConfig(
    navGraph = {
        routeScreen<StartRoute> { StartScreen(it) }
        routeScreen<SourceTableRoute> { SourceTableScreen(it) }
        routeScreen<SourceItemRoute> { SourceItemScreen(it) }
        routeScreen<FeedTableRoute> { FeedTableScreen(it) }
        routeScreen<HelloRoute> { HelloScreen(it) }
        routeScreen<FeedItemRoute> { FeedItemScreen(it) }
        routeScreen<ChapterTableRoute> { ChapterTableScreen(it) }
        routeScreen<ChapterItemRoute> { ChapterItemScreen(it) }
        routeScreen<HostTableRoute> { HostTableScreen(it) }
        routeScreen<HostItemRoute> { HostItemScreen(it) }
        routeScreen<ChartBoardRoute> { ChartBoardScreen(it) }
        routeScreen<StoryTableRoute> { StoryTableScreen(it) }
        routeScreen<StoryItemRoute> { StoryItemScreen(it) }
    }
)