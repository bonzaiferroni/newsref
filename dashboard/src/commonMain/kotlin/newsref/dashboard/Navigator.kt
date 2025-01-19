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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import compose.icons.TablerIcons
import compose.icons.tablericons.FileInfo
import compose.icons.tablericons.Home
import compose.icons.tablericons.Rss
import kotlinx.coroutines.flow.MutableStateFlow
import newsref.dashboard.utils.LocalToolTipper
import newsref.dashboard.utils.ToolTipperModel

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
                    context = context,
                    navigator = viewModel,
                    route = state.route,
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
    context: AppContext,
    route: ScreenRoute?,
    navigator: NavigatorModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(route?.title ?: "Welcome") },
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
            IconButton(onClick = { navigator.go(StartRoute) }) {
                Icon(imageVector = TablerIcons.Home, contentDescription = "Start Screen")
            }
            IconButton(onClick = { navigator.go(SourceTableRoute) }) {
                Icon(imageVector = TablerIcons.FileInfo, contentDescription = "Source Table")
            }
            IconButton(onClick = { navigator.go(FeedTableRoute) }) {
                Icon(imageVector = TablerIcons.Rss, contentDescription = "Feed Table")
            }
            IconButton(onClick = context.exitApp) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close App")
            }
        }
    )
}

val LocalNavigator = compositionLocalOf<NavigatorModel> {
    error("No MyObject provided")
}