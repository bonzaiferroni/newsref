package newsref.dashboard.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.serialization.Serializable
import newsref.dashboard.LocalNavigator
import newsref.dashboard.ScreenRoute
import newsref.dashboard.SourceItemRoute
import newsref.dashboard.baseSpacing
import newsref.dashboard.halfPadding
import newsref.dashboard.halfSpacing
import newsref.dashboard.ui.controls.TabFolder
import newsref.dashboard.ui.controls.TabPage
import newsref.dashboard.ui.table.DurationAgoCell
import newsref.dashboard.ui.table.PropertyRow
import newsref.dashboard.ui.table.PropertyTable
import newsref.dashboard.ui.table.TextCell

@Composable
fun SourceItemScreen(
    route: SourceItemRoute,
    viewModel: SourceItemModel = viewModel { SourceItemModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val item = state.source
    val uriHandler = LocalUriHandler.current
    val nav = LocalNavigator.current

    if (item == null) {
        Text("Fetching Source with id: ${route.sourceId}")
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(baseSpacing)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(halfSpacing),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(halfSpacing),
                    modifier = Modifier.height(IntrinsicSize.Max)
                ) {
                    Box(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(halfPadding)
                            .width(24.dp)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = item.score.toString(),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    item.thumbnail?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            modifier = Modifier.height(50.dp)
                        )
                    }
                }
                Column {
                    SelectionContainer {
                        Text(
                            text = item.title ?: item.url.href,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Text(
                        text = item.url.core,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            TabFolder(
                currentPageName = state.page,
                onChangePage = {
                    viewModel.changePage(it)
                    nav.setRoute(route.copy(pageName = it))
                },
                pages = listOf(
                    TabPage("Data") {
                        SourceDataView(viewModel)
                    },
                    TabPage("Content") {
                        SourceContentView(item)
                    }
                )
            )
        }
    }
}