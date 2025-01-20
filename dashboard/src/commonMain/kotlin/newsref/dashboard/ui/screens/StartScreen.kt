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
import newsref.dashboard.LocalNavigator
import newsref.dashboard.SourceItemRoute
import newsref.dashboard.StartRoute
import newsref.dashboard.baseSpacing
import newsref.dashboard.halfPadding
import newsref.dashboard.halfSpacing
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

    val options = listOf(
        SinceOption("Day", 1.days),
        SinceOption("Week", 7.days),
        SinceOption("Month", 30.days),
    )

    LaunchedEffect(state.since) {
        nav.setRoute(route.copy(days = state.since.inWholeDays.toInt()))
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(baseSpacing)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(baseSpacing)
        ) {
            for (option in options) {
                val alpha = when {
                    option.duration == state.since -> 1f
                    else -> .5f
                }
                Button(
                    onClick = { viewModel.changeSince(option.duration) },
                    modifier = Modifier.alpha(alpha)
                ) {
                    Text(option.name)
                }
            }
        }

        SourceTable(state.sources, nav)
    }
}

data class SinceOption(
    val name: String,
    val duration: Duration
)