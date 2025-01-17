package newsref.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import compose.icons.TablerIcons
import compose.icons.tablericons.FileInfo
import compose.icons.tablericons.Rss
import newsref.dashboard.ui.screens.FeedTableRoute
import newsref.dashboard.ui.screens.SourceTableRoute
import newsref.dashboard.ui.theme.surfaceDark

@Composable
fun AppNavigator(
    startRoute: ScreenRoute,
    context: AppContext,
    navController: NavHostController = rememberNavController()
) {
    val routeState: MutableState<ScreenRoute> = mutableStateOf(startRoute)
    var route: ScreenRoute by remember { routeState }

    Scaffold(
        topBar = {
            TopBar(
                context = context,
                route = route,
                navController = navController,
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                // .verticalScroll(rememberScrollState())
        ) {
            navGraph(routeState, navController)
        }
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
    navController: NavController,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(route.title) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate(SourceTableRoute) }) {
                Icon(imageVector = TablerIcons.FileInfo, contentDescription = "Source Table")
            }
            IconButton(onClick = { navController.navigate(FeedTableRoute) }) {
                Icon(imageVector = TablerIcons.Rss, contentDescription = "Feed Table")
            }
            IconButton(onClick = context.exitApp) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close App")
            }
        }
    )
}