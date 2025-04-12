package newsref.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.collections.immutable.toImmutableList
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
        Spacer(modifier = Modifier.height(50.dp))

        DropMenu(selected = state.feedSpan, items = feedSpans) {
            viewModel.changeSpan(it)
            nav.setRoute(ChapterFeedRoute(it.ordinal))
        }
        BalloonChart(
            selectedId = state.selectedId ?: 0,
            balloons = state.chartConfig,
            height = 400.dp,
            onClickBalloon = viewModel::selectId
        )

        val height = 108f
        CardFeed(
            selectedId = state.selectedId,
            items = state.chapters,
            onSelect = viewModel::selectId,
            getId = { it.id }
        ) { chapter, isSelected ->
            val corner = (height / 2) * density.density
            Card(
                shape = RoundedCornerShape(
                    topStart = corner,
                    topEnd = 0f,
                    bottomStart = corner,
                    bottomEnd = 0f
                ),
                onClick = {
                    nav.go(ChapterRoute(chapter.id, chapter.title))
                },
                modifier = Modifier.fillMaxWidth()
                    .height(height.dp)
            ) {
                val color = Blip.colors.getSwatchFromIndex(chapter.id)
                val pages = remember(chapter.id) { chapter.pages?.toImmutableList() }
                BalloonHeader(
                    color = color,
                    title = chapter.title ?: "Chapter id: ${chapter.id}",
                    imageUrl = chapter.imageUrl,
                    score = chapter.score,
                    height = height,
                    isSelected = isSelected,
                    onSelect = { viewModel.selectId(chapter.id) },
                    storyCount = chapter.size,
                    time = chapter.averageAt,
                    pages = pages
                )
            }
        }
    }
}