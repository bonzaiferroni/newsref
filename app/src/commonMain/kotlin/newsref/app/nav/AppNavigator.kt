package newsref.app.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import newsref.app.StartRoute
import newsref.app.core.AppRoute
import newsref.app.core.Nav
import newsref.app.fui.Fui
import newsref.app.fui.LocalTheme
import newsref.app.fui.Surface
import newsref.app.ui.StartScreen

@Composable
fun AppNavigator(
    startRoute: AppRoute,
    // changeRoute: (AppRoute) -> Unit,
    navController: NavHostController = rememberNavController(),
    viewModel: AppNavigatorModel = viewModel { AppNavigatorModel(startRoute) }
) {
    val state by viewModel.state.collectAsState()

    val destination = state.destination
    LaunchedEffect(destination) {
        if (destination != null) {
            navController.navigate(destination)
        }
    }
    LaunchedEffect(state.route) {
        // changeRoute(state.route)
    }

    CompositionLocalProvider(LocalNav provides viewModel) {
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
        }
    }
}

inline fun <reified T: AppRoute> NavGraphBuilder.routeScreen(
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

val LocalNav = compositionLocalOf<Nav> {
    error("No Nav provided")
}