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
import newsref.model.data.ChapterPageLite
import newsref.model.utils.formatSpanLong

@Composable
fun ChapterScreen(
    route: ChapterRoute,
    viewModel: ChapterModel = viewModel { ChapterModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val chapter = state.chapter
    val articles = chapter?.pages
    if (chapter == null || articles == null) return
    Column(
        verticalArrangement = Blip.ruler.columnTight
    ) {
        BalloonChart(0, state.balloons, 400.dp, { })
        H1(chapter.title ?: "Chapter: ${chapter.id}")
        Text("${chapter.size} sources, ${chapter.averageAt.formatSpanLong()}")
        TabCard(
            initialTab = route.tab ?: "Articles",
            modifyRoute = { route.copy(tab = it) },
        ) {
            Tab("Data") {
                ChapterPropertyRow(chapter)
            }
            Tab("Articles", false) {
                ChapterPagesListView(state.articles, "articles", chapter.id)
            }
            Tab("References", false) {
                ChapterPagesListView(state.references, "references", chapter.id)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChapterPageLiteItem(
    chapterPage: ChapterPageLite,
    chapterId: Long? = null,
    modifier: Modifier = Modifier,
) {
    val nav = LocalNav.current
    val page = chapterPage.page
    Row(
        horizontalArrangement = Blip.ruler.rowTight,
        modifier = modifier
            .clickable {
                if (chapterId != null) {
                    nav.go(ChapterPageRoute(chapterId, page.id, page.headline))
                } else {
                    nav.go(PageRoute(page.id, page.headline))
                }
            }
            .padding(vertical = Blip.ruler.innerSpacing)
    ) {
        val color = Blip.colors.getSwatchFromIndex(page.id)
        ShapeImage(
            color = color,
            url = page.imageUrl,
            padding = PaddingValues(1.dp),
            modifier = Modifier.height(48.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Blip.ruler.rowTight
            ) {
                val uriHandler = LocalUriHandler.current
                H4(page.headline ?: "source: ${page.id}", maxLines = 2, modifier = Modifier.weight(1f))
                Button(
                    onClick = { uriHandler.openUri(page.url) },
                    background = Blip.colors.accent
                ) { Text("Read") }
            }
            FlowRow(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Label("${page.articleType.title} from ${page.existedAt.formatSpanLong()}, visibility: ${page.score}")
                Label(page.hostCore)
            }
            Label("Distance: ${chapterPage.textDistance}")
        }
    }
}