package newsref.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import newsref.dashboard.ui.screens.FeedItemRoute
import newsref.dashboard.ui.screens.FeedItemScreen
import newsref.dashboard.ui.screens.FeedTableRoute
import newsref.dashboard.ui.screens.FeedTableScreen
import newsref.dashboard.ui.screens.HelloRoute
import newsref.dashboard.ui.screens.HelloScreen
import newsref.dashboard.ui.screens.SourceTableRoute
import newsref.dashboard.ui.screens.SourceTableScreen

@Serializable
open class ScreenRoute(val title: String = "Title") {
}

fun NavGraphBuilder.navGraph(
    routeState: MutableState<ScreenRoute>,
    navController: NavHostController
) {
    routeComposable<SourceTableRoute>(routeState) {
        DefaultSurface {
            SourceTableScreen(navController)
        }
    }
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
    routeComposable<FeedItemRoute>(routeState) { route ->
        DefaultSurface {
            FeedItemScreen(route, navController)
        }
    }
}

inline fun <reified T: ScreenRoute> NavGraphBuilder.routeComposable(
    routeState: MutableState<ScreenRoute>,
    crossinline content: @Composable (T) -> Unit
) {
    composable<T> { backStackEntry ->
        val route: T = backStackEntry.toRoute()
        routeState.value = route
        content(route)
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