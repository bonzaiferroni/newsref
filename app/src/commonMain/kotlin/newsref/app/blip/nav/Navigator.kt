package newsref.app.blip.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import newsref.app.blip.behavior.SlideIn
import newsref.app.blip.theme.*
import newsref.app.blip.controls.Surface
import newsref.app.blip.core.BlipConfig

@Composable
fun Navigator(
    startRoute: NavRoute,
    changeRoute: (NavRoute) -> Unit,
    config: BlipConfig,
    exitApp: (() -> Unit)?,
    navController: NavHostController = rememberNavController(),
    nav: NavigatorModel = viewModel { NavigatorModel(startRoute, navController) }
) {
    val state by nav.state.collectAsState()

    LaunchedEffect(state.destination) {
        state.destination?.let { changeRoute(it) }
    }

    CompositionLocalProvider(LocalNav provides nav) {
        Portal(
            currentRoute = state.route,
            config = config,
            exitAction = exitApp
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
    SlideIn {
        ProvideColors(Blip.theme.bookColors) {
            Surface(
                modifier = Modifier
                    .clip(Blip.ruler.roundTop)
                    .background(Blip.colors.surface)
                    .padding(Blip.ruler.basePadding)
            ) {
                Column(verticalArrangement = Blip.ruler.columnSpaced) {
                    content()
                }
            }
        }
    }
}

val LocalNav = staticCompositionLocalOf<Nav> {
    error("No Nav provided")
}