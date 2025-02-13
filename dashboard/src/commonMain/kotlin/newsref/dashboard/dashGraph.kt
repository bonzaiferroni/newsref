package newsref.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import newsref.dashboard.ui.screens.*

fun NavGraphBuilder.dashGraph() {
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

inline fun <reified T: DashRoute> NavGraphBuilder.routeScreen(
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
