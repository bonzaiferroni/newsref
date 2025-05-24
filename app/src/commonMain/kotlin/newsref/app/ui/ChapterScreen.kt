package newsref.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.collections.immutable.ImmutableList
import newsref.app.*
import pondui.ui.controls.*
import pondui.ui.nav.LocalNav
import pondui.ui.nav.Scaffold
import pondui.ui.theme.Pond
import newsref.model.data.ChapterPageLite
import newsref.model.data.ChapterPerson
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
    Scaffold(
        verticalArrangement = Pond.ruler.columnUnit
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
            Tab("People", false) {
                ChapterPersonListView(state.persons)
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
        horizontalArrangement = Pond.ruler.rowUnit,
        modifier = modifier
            .clickable {
                if (chapterId != null) {
                    nav.go(ChapterPageRoute(chapterId, page.id, page.headline))
                } else {
                    nav.go(PageRoute(page.id, page.headline))
                }
            }
            .padding(vertical = Pond.ruler.innerSpacing)
    ) {
        val color = Pond.colors.getSwatchFromIndex(page.id)
        ShapeImage(
            color = color,
            url = page.imageUrl,
            padding = PaddingValues(1.dp),
            modifier = Modifier.height(48.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Pond.ruler.rowUnit
            ) {
                val uriHandler = LocalUriHandler.current
                H4(page.headline ?: "source: ${page.id}", maxLines = 2, modifier = Modifier.weight(1f))
                Button(
                    onClick = { uriHandler.openUri(page.url) },
                    background = Pond.colors.secondary
                ) { Text("Read") }
            }
            FlowRow(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Label("${page.articleType.title} from ${page.existedAt.formatSpanLong()}, visibility: ${page.score}")
                Label(page.hostCore)
            }
            PropertyLabel("Distance", chapterPage.textDistance)
        }
    }
}

@Composable
fun ChapterPersonListView(
    persons: ImmutableList<ChapterPerson>
) {
    LazyColumn(
        verticalArrangement = Pond.ruler.columnGrouped,
    ) {
        items(persons) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(it.name)
                    Text(it.mentions.toString())
                }
                Column {
                    for (identifier in it.identifiers) {
                        Text(identifier)
                    }
                }
            }
        }
    }
}