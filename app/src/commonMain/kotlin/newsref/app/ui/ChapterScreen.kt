package newsref.app.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.ChapterRoute
import newsref.app.blip.controls.Text

@Composable
fun ChapterScreen(
    route: ChapterRoute,
    viewModel: ChapterModel = viewModel { ChapterModel(route)}
) {
    val state by viewModel.state.collectAsState()
    Text(route.chapterTitle ?: "Chapter: ${route.id}")
}