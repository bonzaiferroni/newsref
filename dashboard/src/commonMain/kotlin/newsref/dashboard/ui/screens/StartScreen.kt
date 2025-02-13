package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.dashboard.LocalNavigator
import newsref.dashboard.StartRoute
import newsref.dashboard.baseSpacing
import newsref.dashboard.ui.controls.SinceMenu

@Composable
fun StartScreen(
    route: StartRoute,
    viewModel: StartModel = viewModel { StartModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val nav = LocalNavigator.current

    LaunchedEffect(state.since) {
        nav.setRoute(route.copy(days = state.since.inWholeDays.toInt()))
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(baseSpacing)
    ) {
        SinceMenu(
            state.since,
            viewModel::changeSince
        )

        SourceTable(
            sources = state.sources,
        )
    }
}