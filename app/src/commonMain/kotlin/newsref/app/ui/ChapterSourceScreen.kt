package newsref.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip.ruler

@Composable
fun ChapterSourceScreen(
    route: ChapterSourceRoute,
    viewModel: ChapterSourceModel = viewModel { ChapterSourceModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val source = state.source

    Column(
        verticalArrangement = ruler.columnTight
    ) {
        BalloonChart(
            selectedId = state.selectedId,
            balloons = state.balloons,
            height = 400.dp,
            onClickBalloon = viewModel::selectSource
        )
        if (source == null) return@Column
        val height = 130f
        Card(
            shape = RoundedCornerShape(
                topStart = height / 2,
                topEnd = height / 2,
                bottomStart = 0f,
                bottomEnd = 0f
            ),
            modifier = Modifier.fillMaxWidth()
                .height(height.dp)
        ) {
            BalloonHeader(
                balloonId = source.chapterId,
                title = source.title,
                imageUrl = source.imageUrl,
                score = source.score,
                height = height,
                isSelected = false,
                onSelect = { },
                sources = state.chapter?.sources
            )
        }
        TabCard(
            state.page,
            viewModel::onChangePage,
            pages = pages(
                TabPage("Article", false) { Text("Article content") },
                TabPage("Summary", false) { Text("Summary content") },
            )
        )
        Text("hello chapter source!")
    }

}
