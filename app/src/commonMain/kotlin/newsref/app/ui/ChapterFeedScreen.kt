package newsref.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.*
import newsref.app.blip.controls.*

@Composable
fun ChapterFeedScreen(
    route: ChapterFeedRoute,
    viewModel: ChapterFeedModel = viewModel { ChapterFeedModel(route)}
) {
    val state by viewModel.state.collectAsState()

    DataFeed(state.chapterPacks) { pack ->
        SwatchCard(
            swatchIndex = pack.chapter.id.toInt(),
            modifier = Modifier.fillMaxWidth()
                .height(130.dp)
        ) {
            Row {
                H1(pack.chapter.title ?: "null", maxLines = 2)
            }
            Column {
                val source = pack.sources.first()
                Text(source.title ?: "null", maxLines = 1)
            }
        }
    }
}