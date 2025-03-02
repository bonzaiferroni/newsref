package newsref.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.collections.immutable.persistentListOf
import newsref.app.ChapterRoute
import newsref.app.blip.controls.BalloonChart
import newsref.app.blip.controls.Surface
import newsref.app.blip.controls.Text

@Composable
fun ChapterScreen(
    route: ChapterRoute,
    viewModel: ChapterModel = viewModel { ChapterModel(route)}
) {
    val state by viewModel.state.collectAsState()
    val pack = state.pack
    val headerHeight = 130f
    BalloonChart(0, persistentListOf(), 400.dp) { }
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
        ChapterHeader(
            chapterId = route.id,
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