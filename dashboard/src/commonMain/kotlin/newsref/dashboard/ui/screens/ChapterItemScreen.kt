package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.blip.controls.Tab
import newsref.app.blip.controls.Tabs
import newsref.dashboard.*
import newsref.dashboard.ChapterItemRoute
import newsref.dashboard.LocalNavigator
import newsref.dashboard.SourceItemRoute
import newsref.dashboard.ui.table.*
import newsref.db.model.ChapterSourceInfo
import newsref.db.utils.format
import newsref.model.core.DataSort
import newsref.model.core.Sorting
import newsref.model.utils.*

@Composable
fun ChapterItemScreen(
    route: ChapterItemRoute,
    viewModel: ChapterItemModel = viewModel { ChapterItemModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val nav = LocalNavigator.current
    val item = state.chapter
    if (item == null) {
        Text("Fetching chapter: ${state.chapterId}")
        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(baseSpacing)
    ) {
        PropertyTable(
            name = "Chapter properties",
            item = item,
            properties = listOf(
                textRow("Id", item.id.toString()),
                textRow("Title", item.title, lines = 3),
                textRow("Score", item.score.toString()),
                textRow("Size", item.size.toString()),
                textRow("cohesion", item.cohesion.format(2)),
                textRow("Happened", item.averageAt.agoFormat()),
                textRow("Primaries", state.primaries.size.toString()),
                textRow("Secondaries", state.secondaries.size.toString())
            )
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(baseSpacing)
        ) {
            Button(onClick = {
                val route = state.secondaries.map { it.source.id }.createSpeakRoute()
                route?.let { nav.go(it) }
            }) {
                Text("Speak Chapter")
            }
            item.parentId?.let {
                Button(onClick = { nav.go(ChapterItemRoute(it)) }) { Text("Previous") }
            }
            state.nextChildId?.let {
                Button(onClick = { nav.go(ChapterItemRoute(it))}) { Text("Next")}
            }
        }
        Tabs(
            currentPageName = state.page,
            onChangePage = viewModel::changePage,
        ) {
            Tab(name = "Secondary", scrollable = false) {
                ChapterTable("Secondary Sources", state.secondaries, viewModel::sortSources)
            }
            Tab(name = "Primary", scrollable = false) {
                ChapterTable("Primary Sources", state.primaries, viewModel::sortSources)
            }
            Tab(name = "Children", scrollable = false) {
                ChapterDataTable(state.children, { })
            }
        }

    }
}

@Composable
fun ChapterTable(
    name: String,
    sources: List<ChapterSourceInfo>,
    onSorting: (Sorting) -> Unit
) {
    val nav = LocalNavigator.current
    val uriHandler = LocalUriHandler.current

    DataTable(
        name = name,
        items = sources,
        onSorting = onSorting,
        columnGroups = listOf(
            columns(
                TableColumn(
                    name = "Score", width = 60, align = AlignCell.Right
                ) { TextCell(it.source.score) },
                TableColumn(
                    name = "Headline", weight = 1f,
                    onClickCell = { nav.go(SourceItemRoute(it.source.id)) }
                ) { TextCell(it.source.title) },
                TableColumn(
                    name = "Exst", width = 60, align = AlignCell.Right
                ) { TextCell(it.source.existedAt.agoFormat()) },
            ),
            columns(
                TableColumn(
                    name = "Url", weight = 1f, alpha = .8f,
                    onClickCell = { uriHandler.openUri(it.source.url.href) },
                    controls = listOf(copyText { it.source.url.href })
                ) { TextCell(it.source.url.href) },
                TableColumn(
                    name = "Text", width = 60, align = AlignCell.Right, sort = DataSort.Score
                ) { TextCell(it.chapterSource.textDistance) },
                TableColumn(
                    name = "Link", width = 60, align = AlignCell.Right, sort = DataSort.Score
                ) { TextCell(it.chapterSource.linkDistance) },
                TableColumn(
                    name = "Time", width = 60, align = AlignCell.Right, sort = DataSort.Score
                ) { TextCell(it.chapterSource.timeDistance) },
                TableColumn(
                    name = "Dist", width = 60, align = AlignCell.Right, sort = DataSort.Score
                ) { TextCell(it.chapterSource.distance) },

            )
        )
    )
}