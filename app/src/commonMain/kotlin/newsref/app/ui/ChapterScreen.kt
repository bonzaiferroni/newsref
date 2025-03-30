package newsref.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import newsref.model.utils.formatSpanLong

@Composable
fun ChapterScreen(
    route: ChapterRoute,
    viewModel: ChapterModel = viewModel { ChapterModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val pack = state.pack
    if (pack == null) return
    val (chapter, articles) = pack
    Column(
        verticalArrangement = Blip.ruler.columnTight
    ) {
        BalloonChart(0, state.balloons, 400.dp, { })
        H1(chapter.title ?: "Chapter: ${chapter.id}")
        Text("${chapter.size} sources, ${chapter.averageAt.formatSpanLong()}")
        TabCard(
            currentTab = state.tab,
            onChangePage = viewModel::changeTab,
        ) {
            Tab("Data") {
                ChapterPropertyRow(chapter)
            }
            Tab("Articles", false) {
                SourcesListView(state.articles, "articles", chapter.id)
            }
            Tab("References", false) {
                SourcesListView(state.references, "references", chapter.id)
            }
        }
    }
}

@Composable
fun SourceBitItem(
    source: SourceBit,
    chapterId: Long? = null,
    modifier: Modifier = Modifier,
) {
    val nav = LocalNav.current
    Row(
        horizontalArrangement = Blip.ruler.rowTight,
        modifier = modifier
            .clickable {
                if (chapterId != null)
                    nav.go(ChapterSourceRoute(chapterId, source.id, source.headline))
            }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Blip.ruler.rowTight
            ) {
                val uriHandler = LocalUriHandler.current
                H4(source.headline ?: "source: ${source.id}", maxLines = 2, modifier = Modifier.weight(1f))
                Button(
                    onClick = { uriHandler.openUri(source.url) },
                    background = Blip.colors.accent
                ) { Text("Read") }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Label("${source.articleType.title} from ${source.existedAt.formatSpanLong()}, visibility: ${source.score}")
                Label(source.hostCore)
            }
        }
    }
}