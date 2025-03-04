package newsref.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideSkyColors
import newsref.app.model.ChapterPack
import newsref.app.model.ChapterSource

@Composable
fun ChapterSourceScreen(
    route: ChapterSourceRoute,
    viewModel: ChapterSourceModel = viewModel { ChapterSourceModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val source = state.source
    val chapter = state.chapter

    Column(
        verticalArrangement = Blip.ruler.columnTight
    ) {
        BalloonChart(
            selectedId = state.selectedId,
            balloons = state.balloons,
            height = 400.dp,
            onClickBalloon = viewModel::selectSource
        )
        if (source == null || chapter == null) return@Column
        val height = 130f
        SourceHeader(height, source, chapter)
        TabCard(
            state.page,
            viewModel::onChangePage,
            pages = pages(
                TabPage("Article", false) { Text("Article content") },
                TabPage("Summary", false, source.summary != null) { Text(source.summary!!) },
            )
        )
        Text("hello chapter source!")
    }
}

@Composable
fun SourceHeader(height: Float, source: ChapterSource, chapter: ChapterPack) {
    Card(
        shape = RoundedCornerShape(
            topStart = height / 2,
            topEnd = height / 2,
            bottomStart = 0f,
            bottomEnd = 0f
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Blip.ruler.columnTight
        ) {
            val color = Blip.colors.getSwatchFromIndex(source.chapterId)
            BalloonHeader(
                color = color,
                title = source.title ?: "Source id: ${source.pageId}",
                imageUrl = source.imageUrl,
                score = source.score,
                height = height,
                isSelected = false,
                onSelect = { },
                sources = chapter.sources
            )
            Row(
                horizontalArrangement = Blip.ruler.rowSpaced,
                modifier = Modifier.height(height.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(height.dp)
                ) {

                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxHeight()
                        .weight(1f)
                        .shadow(Blip.ruler.shadowElevation, RoundedCornerShape(height / 2))
                        .background(Blip.colors.accent)
                        .clickable { println("open in browser") }
                ) {
                    ProvideSkyColors {
                        H2(
                            text = "Read at\n${source.hostCore}",
                            style = TextStyle(textAlign = TextAlign.Center)
                        )
                    }
                }
                Column(
                    modifier = Modifier.size(height.dp)
                ) {

                }
            }
        }
    }
}
