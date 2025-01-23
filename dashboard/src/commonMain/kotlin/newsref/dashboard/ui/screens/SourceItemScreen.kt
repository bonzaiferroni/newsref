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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import newsref.dashboard.LocalNavigator
import newsref.dashboard.SourceItemRoute
import newsref.dashboard.baseSpacing
import newsref.dashboard.halfPadding
import newsref.dashboard.halfSpacing
import newsref.dashboard.ui.controls.TabFolder
import newsref.dashboard.ui.controls.TabPage
import newsref.model.data.Source

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
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(baseSpacing)
        ) {
            ItemHeader(source)
            TabFolder(
                currentPageName = state.page,
                onChangePage = {
                    viewModel.changePage(it)
                    nav.setRoute(route.copy(pageName = it))
                },
                pages = mutableListOf(
                    TabPage("Data") {
                        SourceDataView(viewModel)
                    },
                    TabPage("Content") {
                        SourceContentView(source, state.contents)
                    },
                ).apply {
                    if (state.inbound.isNotEmpty()) this.add(
                        TabPage("Inbound", false) {
                            LinkInfoView("Inbound Links", state.inbound)
                        }
                    )
                }
            )
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