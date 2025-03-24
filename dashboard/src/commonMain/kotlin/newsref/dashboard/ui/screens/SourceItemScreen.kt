package newsref.dashboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import newsref.app.blip.controls.Tab
import newsref.app.blip.controls.Tabs
import newsref.dashboard.*
import newsref.dashboard.LocalNavigator
import newsref.dashboard.SourceItemRoute
import newsref.db.model.Source

@Composable
fun SourceItemScreen(
    route: SourceItemRoute,
    viewModel: SourceItemModel = viewModel { SourceItemModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val source = state.source
    val nav = LocalNavigator.current

    if (source == null) {
        Text("Fetching Source with id: ${route.sourceId}")
        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(baseSpacing)
    ) {
        ItemHeader(source)
        Tabs(
            currentPageName = state.page,
            onChangePage = {
                viewModel.changePage(it)
                nav.setRoute(route.copy(pageName = it))
            }
        ) {
            Tab("Data") {
                SourceDataView(viewModel)
            }
            Tab("Content", false) {
                SourceContentView(source, state.contents, route)
            }
            Tab("Inbound", false, state.inbound.isNotEmpty()) {
                LinkInfoView("Inbound Links", state.inbound)
            }
            Tab("Outbound", false, state.outbound.isNotEmpty()) {
                LinkInfoView("Outbound Links", state.outbound)
            }
            Tab("Distances", false, state.distances.isNotEmpty()) {
                SourceDistanceView(state.distances)
            }
        }
    }
}

@Composable
fun ItemHeader(
    source: Source
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
                    text = (source.score ?: 0).toString(),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            source.thumbnail?.let {
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
                    text = source.title ?: source.url.href,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Text(
                text = source.url.core,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}