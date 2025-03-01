package newsref.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
    BalloonChart(0, persistentListOf(), 400.dp) { }
    Card(
        modifier = Modifier.fillMaxWidth()
            .heightIn(min = 400.dp)
    ) {
        Text(route.chapterTitle ?: "Chapter: ${route.id}")
        val score = state.pack?.chapter?.score.toString()
        Text(score)
    }
}