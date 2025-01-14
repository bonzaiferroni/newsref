package newsref.dashboard

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable
import newsref.dashboard.ui.FeedRowScreen
import newsref.dashboard.ui.FeedTableScreen
import newsref.dashboard.ui.HelloScreen

@Serializable
object StartRoute : ScreenRoute("Start")

@Serializable
data class HelloRoute(val name: String) : ScreenRoute("Hello")

@Serializable
object FeedTableRoute : ScreenRoute("Feed Table")

@Serializable
data class FeedRowRoute(val id: Int) : ScreenRoute("Feed Row")

@Serializable
open class ScreenRoute(val title: String = "Title") {
}

fun NavGraphBuilder.navGraph(
    routeState: MutableState<ScreenRoute>,
    navController: NavHostController
) {
    routeComposable<FeedTableRoute>(routeState) {
        DefaultSurface {
            FeedTableScreen(navController)
        }
    }
    routeComposable<HelloRoute>(routeState) { route ->
        DefaultSurface {
            HelloScreen(route, navController)
        }
    }
    routeComposable<FeedRowRoute>(routeState) { route ->
        DefaultSurface {
            FeedRowScreen(route, navController)
        }
    }
}