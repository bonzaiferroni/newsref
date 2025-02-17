package newsref.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip

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
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    H1(pack.chapter.title ?: "null", maxLines = 2)
                    val source = pack.sources.first()
                    Text(source.title ?: "null", maxLines = 1)
                }
                val imgUrl = pack.sources.firstOrNull { it.imageUrl != null }?.imageUrl
                imgUrl?.let {
                    val color = Blip.colors.getSwatchFromIndex(pack.chapter.id)
                    Box(
                        modifier = Modifier.size(100.dp)
                            .shadow(10.dp, Blip.ruler.round)
                            .background(color)
                            .padding(Blip.ruler.innerPadding)
                    ) {
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(Blip.ruler.round)
                        )
                    }
                }
            }
        }
    }
}