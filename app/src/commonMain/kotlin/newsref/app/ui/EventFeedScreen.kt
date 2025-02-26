package newsref.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideBookColors
import newsref.app.blip.theme.ProvideSkyColors

@Composable
fun EventFeedScreen(
    route: EvemtFeedRoute,
    viewModel: EventFeedModel = viewModel { EventFeedModel(route)}
) {
    val state by viewModel.state.collectAsState()

    Column(
        verticalArrangement = Blip.ruler.columnSpaced
    ) {
        BalloonChart(
            state.balloonPoints,
            400.dp,
            { }
        )

        DataFeed(state.chapterPacks) { pack ->
            EventCard(
                modifier = Modifier.fillMaxWidth()
                    .height(130.dp)
            ) {
                Row(
                    horizontalArrangement = Blip.ruler.rowSpaced
                ) {
                    val color = Blip.colors.getSwatchFromIndex(pack.chapter.id)
                    val sizeFactor = pack.chapter.score / 100f
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxHeight()
                            .aspectRatio(1f)
                            .drawBehind {
                                val radius = ((size.minDimension / 2) - 10) * sizeFactor + 10
                                drawCircle(
                                    color = color.copy(.75f),
                                    radius = radius
                                )
                                drawCircle(
                                    color = color,
                                    radius = radius,
                                    style = Stroke(width = 2.dp.toPx()) // Stroke style
                                )
                            }
                    ) {
                        ProvideSkyColors {
                            H2(pack.chapter.score.toString())
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                            .fillMaxHeight()
                    ) {
                        H1(pack.chapter.title ?: "null", maxLines = 2)
                        val source = pack.sources.first()
                        Text(source.title ?: "null", maxLines = 1)
                    }
                    val imgUrl = pack.sources.firstOrNull { it.imageUrl != null }?.imageUrl
                    imgUrl?.let {

                        Box(
                            modifier = Modifier.fillMaxHeight()
                                .aspectRatio(1f)
                                .shadow(8.dp, Blip.ruler.round)
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
}

@Composable
fun EventCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ProvideBookColors {
        val shape = RoundedCornerShape(
            topStartPercent = 50,
            topEndPercent = 50,
            bottomStartPercent = 50,
            bottomEndPercent = 50
        )
        Column (
            modifier = modifier
                .bg(Blip.localColors.surface, shape)
                .padding(Blip.ruler.halfPadding)
        ) {
            content()
        }
    }
}