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
import newsref.dashboard.ui.screens.FeedItemScreen
import newsref.dashboard.ui.screens.FeedTableScreen
import newsref.dashboard.ui.screens.HelloScreen
import newsref.dashboard.ui.screens.SourceItemScreen
import newsref.dashboard.ui.screens.SourceTableScreen
import newsref.dashboard.ui.screens.StartScreen

fun NavGraphBuilder.navGraph() {
    routeComposable<StartRoute> {
        DefaultSurface {
            StartScreen(StartRoute())
        }
    }
    routeComposable<SourceTableRoute> {
        DefaultSurface {
            SourceTableScreen()
        }
    }
    routeComposable<SourceItemRoute> { route ->
        DefaultSurface {
            SourceItemScreen(route)
        }
    }
    routeComposable<FeedTableRoute> {
        DefaultSurface {
            FeedTableScreen()
        }
    }
    routeComposable<HelloRoute> { route ->
        DefaultSurface {
            HelloScreen(route)
        }
    }
    routeComposable<FeedItemRoute> { route ->
        DefaultSurface {
            FeedItemScreen(route)
        }
    }
}

inline fun <reified T: ScreenRoute> NavGraphBuilder.routeComposable(
    crossinline content: @Composable (T) -> Unit
) {
    composable<T> { backStackEntry ->
        val route: T = backStackEntry.toRoute()
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