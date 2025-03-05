package newsref.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.Clock
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip
import newsref.app.model.SourceBit

@Composable
fun ChapterScreen(
    route: ChapterRoute,
    viewModel: ChapterModel = viewModel { ChapterModel(route)}
) {
    val state by viewModel.state.collectAsState()
    val pack = state.pack
    if (pack == null) return
    BalloonChart(0, state.balloons, 400.dp, { })
    H1(pack.chapter.title ?: "Chapter: ${pack.chapter.id}")
    TabCard(
        currentPageName = state.tab,
        onChangePage = viewModel::changeTab,
        pages = pages(
            TabPage("Articles", false) {
                LazyColumn(
                    verticalArrangement = Blip.ruler.columnSpaced
                ) {
                    items(state.articles) {
                        SourceHeadline(it)
                    }
                }
            },
            TabPage("Other Sources", false) {
                LazyColumn(
                    verticalArrangement = Blip.ruler.columnTight
                ) {
                    items(state.references) {
                        SourceHeadline(it)
                    }
                }
            }
        )
    )
}

@Composable
fun SourceHeadline(source: SourceBit) {
    Row(
        horizontalArrangement = Blip.ruler.rowTight,
        modifier = Modifier.height(48.dp)
    ) {
        val color = Blip.colors.getSwatchFromIndex(source.id)
        source.imageUrl?.let {
            HeaderImage(color, it, PaddingValues(1.dp))
        }
        H3(source.title ?: "source: ${source.id}")
    }
}