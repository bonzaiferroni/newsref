package newsref.app.blip.nav

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import newsref.app.blip.theme.*
import newsref.app.blip.controls.Surface
import newsref.app.blip.core.BlipConfig

@Composable
fun Navigator(
    startRoute: NavRoute,
    changeRoute: (NavRoute) -> Unit,
    config: BlipConfig,
    navController: NavHostController = rememberNavController(),
    nav: NavigatorModel = viewModel { NavigatorModel(startRoute, navController, changeRoute) }
) {
    CompositionLocalProvider(LocalNav provides nav) {
        Portal(
            logo = config.logo,
            logoAction = { nav.go(config.home) },
            actions = config.portalActions
        ) {
            NavHost(
                navController = navController,
                startDestination = startRoute,
                modifier = Modifier
                    // .padding(innerPadding)
                    .fillMaxSize()
                // .background(MaterialTheme.colorScheme.surface)
                // .verticalScroll(rememberScrollState())
            ) {
                config.navGraph(this)
            }
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
            .padding(Blip.layout.basePadding)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Blip.layout.halfSpacing)) {
            content()
        }
    }
}

val LocalNav = staticCompositionLocalOf<Nav> {
    error("No Nav provided")
}