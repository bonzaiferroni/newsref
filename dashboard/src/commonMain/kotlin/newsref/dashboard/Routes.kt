package newsref.dashboard

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable
import newsref.dashboard.ui.HelloScreen
import newsref.dashboard.ui.StartScreen

@Serializable
object StartRoute : ScreenRoute("Start")

@Serializable
data class HelloRoute(val name: String) : ScreenRoute("Hello")

@Serializable
open class ScreenRoute(val title: String = "Title") {
}

fun NavGraphBuilder.navGraph(
    routeState: MutableState<ScreenRoute>,
    navController: NavHostController
) {
    routeComposable<StartRoute>(routeState) {
        DefaultScaffold {
            StartScreen(navController)
        }
    }
    routeComposable<HelloRoute>(routeState) { route ->
        DefaultScaffold {
            HelloScreen(route, navController)
        }
    }
}