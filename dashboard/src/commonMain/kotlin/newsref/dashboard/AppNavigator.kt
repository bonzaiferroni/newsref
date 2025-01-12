package newsref.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import newsref.dashboard.ui.HelloScreen
import newsref.dashboard.ui.StartScreen
import newsref.dashboard.ui.theme.surfaceDark

@Composable
fun AppNavigator(
    context: AppContext,
    navController: NavHostController = rememberNavController()
) {
    val routeState: MutableState<ScreenRoute> = mutableStateOf(StartRoute)
    var route: ScreenRoute by remember { routeState }

    Scaffold(
        topBar = {
            TopBar(
                context = context,
                route = route,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = StartRoute,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(surfaceDark)
                .verticalScroll(rememberScrollState())
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
fun DefaultScaffold(
    padding: PaddingValues = basePadding,
    content: @Composable() () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(padding)
    ) {
        content()
    }
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    context: AppContext,
    route: ScreenRoute,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(route.title) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = context.exitApp) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close App")
            }
        }
    )
}