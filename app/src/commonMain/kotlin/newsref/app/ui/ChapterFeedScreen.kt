package newsref.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
            Row(
                horizontalArrangement = Blip.ruler.rowSpaced
            ) {
                Column(
                    verticalArrangement = Blip.ruler.columnGrouped
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                    ) {
                        H2(pack.chapter.score.toString())
                        Label("visibility")
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                    ) {
                        H2(pack.chapter.size.toString())
                        Label("articles")
                    }
                }
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
                        modifier = Modifier.fillMaxHeight()
                            .aspectRatio(1f)
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