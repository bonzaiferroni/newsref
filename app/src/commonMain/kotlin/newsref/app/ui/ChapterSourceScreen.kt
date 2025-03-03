package newsref.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip.ruler

@Composable
fun ChapterSourceScreen(
    route: ChapterSourceRoute,
    viewModel: ChapterSourceModel = viewModel { ChapterSourceModel(route) }
) {
    val state by viewModel.state.collectAsState()

    Column(
        verticalArrangement = ruler.columnSpaced
    ) {
        BalloonChart(
            selectedId = state.selectedId,
            balloons = state.balloons,
            height = 400.dp,
            onClickBalloon = { }
        )

        Text("hello chapter source!")
    }

}
