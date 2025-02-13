package newsref.dashboard.nav

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import newsref.dashboard.ChapterItemRoute
import newsref.dashboard.ChapterTableRoute
import newsref.dashboard.ChartBoardRoute
import newsref.dashboard.FeedItemRoute
import newsref.dashboard.FeedTableRoute
import newsref.dashboard.HelloRoute
import newsref.dashboard.HostItemRoute
import newsref.dashboard.HostTableRoute
import newsref.dashboard.ScreenRoute
import newsref.dashboard.SourceItemRoute
import newsref.dashboard.SourceTableRoute
import newsref.dashboard.StartRoute
import newsref.dashboard.StoryItemRoute
import newsref.dashboard.StoryTableRoute
import newsref.dashboard.basePadding
import newsref.dashboard.halfSpacing
import newsref.dashboard.ui.screens.*

fun NavGraphBuilder.navGraph() {
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

inline fun <reified T: ScreenRoute> NavGraphBuilder.routeScreen(
    defaultSurface: Boolean = true,
    crossinline content: @Composable (T) -> Unit
) {
    composable<T> { backStackEntry ->
        val route: T = backStackEntry.toRoute()
        if (defaultSurface) {
            DefaultSurface {
                content(route)
            }
        } else {
            content(route)
        }
    }
}

@Composable
fun DefaultSurface(
    padding: PaddingValues = basePadding,
    content: @Composable() () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(padding)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(halfSpacing)) {
            content()
        }
    }
}
