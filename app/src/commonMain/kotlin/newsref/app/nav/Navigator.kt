package newsref.app.nav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import newsref.app.HelloRoute
import newsref.app.StartRoute
import newsref.app.fui.Fui
import newsref.app.fui.Surface
import newsref.app.ui.HelloScreen
import newsref.app.ui.StartScreen

@Composable
fun Navigator(
    startRoute: NavRoute,
    changeRoute: (NavRoute) -> Unit,
    navController: NavHostController = rememberNavController(),
    nav: NavigatorModel = viewModel { NavigatorModel(startRoute, navController, changeRoute) }
) {
    CompositionLocalProvider(LocalNav provides nav) {
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier
                // .padding(innerPadding)
                .fillMaxSize()
            // .background(MaterialTheme.colorScheme.surface)
            // .verticalScroll(rememberScrollState())
        ) {
            routeScreen<StartRoute> { StartScreen(it) }
            routeScreen<HelloRoute> { HelloScreen(it) }
        }
    }
}

inline fun <reified T: NavRoute> NavGraphBuilder.routeScreen(
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
    content: @Composable() () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(Fui.layout.basePadding)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Fui.layout.halfSpacing)) {
            content()
        }
    }
}

val LocalNav = staticCompositionLocalOf<Nav> {
    error("No Nav provided")
}