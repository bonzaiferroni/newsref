package newsref.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.nav.LocalNav
import newsref.app.blip.theme.Blip
import newsref.app.model.SourceBit

@Composable
fun ChapterScreen(
    route: ChapterRoute,
    viewModel: ChapterModel = viewModel { ChapterModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val pack = state.pack
    if (pack == null) return
    val (chapter, sources) = pack
    Column(
        verticalArrangement = Blip.ruler.columnTight
    ) {
        BalloonChart(0, state.balloons, 400.dp, { })
        H1(chapter.title ?: "Chapter: ${chapter.id}")
        Text("${chapter.size} sources, ${chapter.averageAt.agoLongFormat()} ago")
        TabCard(
            currentTab = state.tab,
            onChangePage = viewModel::changeTab,
            pages = pages(
                TabPage("Articles", false) {
                    LazyColumn(
                    ) {
                        items(state.articles) {
                            SourceHeadline(chapter.id, it)
                        }
                    }
                },
                TabPage("Other Sources", false) {
                    LazyColumn(
                    ) {
                        items(state.references) {
                            SourceHeadline(chapter.id, it)
                        }
                    }
                }
            )
        )
    }
}

@Composable
fun SourceHeadline(chapterId: Long, source: SourceBit) {
    val nav = LocalNav.current
    Row(
        horizontalArrangement = Blip.ruler.rowTight,
        modifier = Modifier
            .clickable { nav.go(ChapterSourceRoute(chapterId, source.id, source.title)) }
            .padding(vertical = Blip.ruler.innerSpacing)
    ) {
        val color = Blip.colors.getSwatchFromIndex(source.id)
        ShapeImage(
            color = color,
            url = source.imageUrl,
            padding = PaddingValues(1.dp),
            modifier = Modifier.height(48.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            H4(source.title ?: "source: ${source.id}", maxLines = 2)
            Text("${source.hostCore}, visibility: ${source.score}")
        }
        val uriHandler = LocalUriHandler.current
        Button(
            onClick = { uriHandler.openUri(source.url) },
            background = Blip.colors.accent
        ) { Text("Read") }
    }
}