package newsref.dashboard.ui.screens

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.dashboard.StoryItemRoute
import newsref.dashboard.ui.table.*

@Composable
fun StoryItemScreen(
    route: StoryItemRoute,
    viewModel: StoryItemModel = viewModel { StoryItemModel(route)}
) {
    val state by viewModel.state.collectAsState()
    val story = state.story
    val chapters = state.chapters
    if (story == null || chapters == null ) {
        Text("fetching story: ${state.storyId}")
        return
    }

    PropertyTable(
        name = "Story",
        item = story,
        properties = listOf(
            textRow("Title", story.title),
            textRow("Size", story.size.toString()),
            textRow("Score", story.score.toString()),
        )
    )

    ChapterDataTable(
        chapters = chapters,
        changeSort = { }
    )
}