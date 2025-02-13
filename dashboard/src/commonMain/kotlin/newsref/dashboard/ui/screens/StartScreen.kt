package newsref.dashboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import newsref.dashboard.LocalNavigator
import newsref.dashboard.SourceItemRoute
import newsref.dashboard.StartRoute
import newsref.dashboard.baseSpacing
import newsref.dashboard.halfPadding
import newsref.dashboard.halfSpacing
import newsref.dashboard.ui.controls.SinceMenu
import org.jetbrains.exposed.sql.Column
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

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