package newsref.app.ui

import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.nav.*

@Composable
fun ChapterFeedScreen(
    route: ChapterFeedRoute,
    viewModel: ChapterFeedModel = viewModel { ChapterFeedModel(route)}
) {
    val state by viewModel.state.collectAsState()

    DataFeed(state.chapterPacks) {
        Text(it.chapter.title ?: "null")
    }
}