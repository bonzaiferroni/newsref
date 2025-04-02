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
import newsref.dashboard.PageItemRoute
import newsref.db.model.Page

@Composable
fun PageItemScreen(
    route: PageItemRoute,
    viewModel: PageItemModel = viewModel { PageItemModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val source = state.page
    val nav = LocalNavigator.current

    if (source == null) {
        Text("Fetching Source with id: ${route.pageId}")
        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(baseSpacing)
    ) {
        ItemHeader(source)
        Tabs(
            currentPageName = state.tab,
            onChangePage = {
                viewModel.changePage(it)
                nav.setRoute(route.copy(pageName = it))
            }
        ) {
            Tab("Data") {
                PageDataView(viewModel)
            }
            Tab("Content", false) {
                PageContentView(source, state.contents, route)
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
    page: Page
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
                    text = (page.score ?: 0).toString(),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            page.thumbnail?.let {
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
                    text = page.title ?: page.url.href,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Text(
                text = page.url.core,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}