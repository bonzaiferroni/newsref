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
import newsref.dashboard.PageItemRoute
import newsref.dashboard.ui.table.*
import newsref.db.model.ChapterPageInfo
import newsref.db.utils.format
import newsref.model.data.DataSort
import newsref.model.data.Sorting
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
                textRow("Happened", item.averageAt.formatSpanBrief()),
                textRow("Primaries", state.primaries.size.toString()),
                textRow("Secondaries", state.secondaries.size.toString())
            )
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(baseSpacing)
        ) {
            Button(onClick = {
                val route = state.secondaries.map { it.page.id }.createSpeakRoute()
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
        Tabs {
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
    sources: List<ChapterPageInfo>,
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
                ) { TextCell(it.page.score) },
                TableColumn(
                    name = "Headline", weight = 1f,
                    onClickCell = { nav.go(PageItemRoute(it.page.id)) }
                ) { TextCell(it.page.title) },
                TableColumn(
                    name = "Exst", width = 60, align = AlignCell.Right
                ) { TextCell(it.page.existedAt.formatSpanBrief()) },
            ),
            columns(
                TableColumn(
                    name = "Url", weight = 1f, alpha = .8f,
                    onClickCell = { uriHandler.openUri(it.page.url.href) },
                    controls = listOf(copyText { it.page.url.href })
                ) { TextCell(it.page.url.href) },
                TableColumn(
                    name = "Text", width = 60, align = AlignCell.Right, sort = DataSort.Score
                ) { TextCell(it.chapterPage.textDistance) },
                TableColumn(
                    name = "Link", width = 60, align = AlignCell.Right, sort = DataSort.Score
                ) { TextCell(it.chapterPage.linkDistance) },
                TableColumn(
                    name = "Time", width = 60, align = AlignCell.Right, sort = DataSort.Score
                ) { TextCell(it.chapterPage.timeDistance) },
                TableColumn(
                    name = "Dist", width = 60, align = AlignCell.Right, sort = DataSort.Score
                ) { TextCell(it.chapterPage.distance) },

            )
        )
    )
}