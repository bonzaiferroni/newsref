package newsref.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
        ) {
            Row {
                H1(pack.chapter.title ?: "null")
            }
            Column {
                for (source in pack.sources) {
                    Text(source.title ?: "null")
                }
            }
        }
    }
}