package newsref.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    Column(
        verticalArrangement = ruler.columnSpaced
    ) {
        BalloonChart(
            selectedId = state.selectedId ?: 0,
            points = state.balloonPoints,
            height = 400.dp,
            onClickCloud = viewModel::selectId
        )

        val height = 130f
        CardFeed(
            selectedId = state.selectedId,
            items = state.chapterPacks,
            onSelect = viewModel::selectId,
            getId = { it.chapter.id }
        ) { pack, isSelected ->
            ChapterCard(
                pack = pack,
                height = height,
                isSelected = isSelected,
                onSelect = { viewModel.selectId(pack.chapter.id) },
                onClick = { nav.go(ChapterRoute(pack.chapter.id, pack.chapter.title))}
            )
        }
    }
}