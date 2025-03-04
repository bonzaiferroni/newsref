package newsref.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip

@Composable
fun ChapterScreen(
    route: ChapterRoute,
    viewModel: ChapterModel = viewModel { ChapterModel(route)}
) {
    val state by viewModel.state.collectAsState()
    val pack = state.pack
    val headerHeight = 130f
    BalloonChart(0, state.balloons, 400.dp, { })
    Card(
        shape = RoundedCornerShape(
            topStart = headerHeight / 2,
            topEnd = headerHeight / 2,
            bottomStart = headerHeight / 2,
            bottomEnd = headerHeight / 2
        ),
        modifier = Modifier.fillMaxWidth()
            .heightIn(min = 400.dp)
    ) {
        val color = Blip.colors.getSwatchFromIndex(route.id)
        BalloonHeader(
            color = color,
            title = pack?.chapter?.title ?: route.chapterTitle ?: "Chapter: ${route.id}",
            imageUrl = pack?.imageUrl,
            score = pack?.chapter?.score ?: 0,
            height = headerHeight,
            isSelected = false,
            onSelect = { },
            sources = pack?.sources
        )
    }
}