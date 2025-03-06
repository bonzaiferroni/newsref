package newsref.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.nav.LocalNav
import newsref.app.blip.theme.Blip

@Composable
fun ChapterFeedScreen(
    route: ChapterFeedRoute,
    viewModel: ChapterFeedModel = viewModel { ChapterFeedModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val ruler = Blip.ruler
    val nav = LocalNav.current

    val density = LocalDensity.current

    Column(
        verticalArrangement = ruler.columnSpaced
    ) {
        DropMenu(selected = state.timeSpan, onSelect = viewModel::changeSpan, items = timeSpans)
        BalloonChart(
            selectedId = state.selectedId ?: 0,
            balloons = state.chartConfig,
            height = 400.dp,
            onClickBalloon = viewModel::selectId
        )

        val height = 108f
        CardFeed(
            selectedId = state.selectedId,
            items = state.chapterPacks,
            onSelect = viewModel::selectId,
            getId = { it.chapter.id }
        ) { pack, isSelected ->
            val corner = (height / 2) * density.density
            Card(
                shape = RoundedCornerShape(
                    topStart = corner,
                    topEnd = 0f,
                    bottomStart = corner,
                    bottomEnd = 0f
                ),
                onClick = {
                    nav.go(ChapterRoute(pack.chapter.id, pack.chapter.title))
                },
                modifier = Modifier.fillMaxWidth()
                    .height(height.dp)
            ) {
                val color = Blip.colors.getSwatchFromIndex(pack.chapter.id)
                BalloonHeader(
                    color = color,
                    title = pack.chapter.title ?: "Chapter id: ${pack.chapter.id}",
                    imageUrl = pack.imageUrl,
                    score = pack.chapter.score,
                    height = height,
                    isSelected = isSelected,
                    onSelect = { viewModel.selectId(pack.chapter.id) },
                    storyCount = pack.chapter.size,
                    time = pack.chapter.averageAt,
                    sources = pack.sources
                )
            }
        }
    }
}