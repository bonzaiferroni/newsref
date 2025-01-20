package newsref.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import compose.icons.TablerIcons
import compose.icons.tablericons.FileInfo
import compose.icons.tablericons.Home
import compose.icons.tablericons.Rss
import newsref.dashboard.utils.modifyIfNotNull

@Composable
fun Navigator(
    startRoute: ScreenRoute,
    context: AppContext,
    changeRoute: (ScreenRoute) -> Unit,
    navController: NavHostController = rememberNavController(),
    viewModel: NavigatorModel = viewModel { NavigatorModel(startRoute) }
) {
    val state by viewModel.state.collectAsState()

    val destination = state.destination
    LaunchedEffect(destination) {
        if (destination != null) {
            navController.navigate(destination)
        }
    }
    LaunchedEffect(state.route) {
        changeRoute(state.route)
    }

    CompositionLocalProvider(LocalNavigator provides viewModel) {
        Scaffold(
            topBar = {
                TopBar(
                    navigator = viewModel,
                    navController = navController,
                    links = listOf(
                        TopBarLink(TablerIcons.Home, StartRoute()),
                        TopBarLink(TablerIcons.FileInfo, SourceTableRoute),
                        TopBarLink(TablerIcons.Rss, FeedTableRoute),
                        TopBarLink(Icons.Default.Close) { context.exitApp() }
                    )
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
                navGraph()
            }
        }
    }
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navigator: NavigatorModel,
    navController: NavController,
    links: List<TopBarLink>,
    modifier: Modifier = Modifier
) {
    val state by navigator.state.collectAsState()

    TopAppBar(
        title = { Text(state.route.title) },
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
            for (link in links) {
                val color = when {
                    link.route?.title == state.route.title -> Color.White.copy(alpha = 1f)
                    else -> Color.White.copy(alpha = .5f)
                }
                IconButton(
                    onClick = {
                        if (link.route != null) navigator.go(link.route)
                        else if (link.onClick != null) link.onClick()
                    }
                ) {
                    Icon(
                        imageVector = link.icon,
                        contentDescription = null,
                        tint = color
                    )
                }
            }
        }
    )
}

data class TopBarLink(
    val icon: ImageVector,
    val route: ScreenRoute? = null,
    val onClick: (() -> Unit)? = null,
)

val LocalNavigator = compositionLocalOf<NavigatorModel> {
    error("No MyObject provided")
}