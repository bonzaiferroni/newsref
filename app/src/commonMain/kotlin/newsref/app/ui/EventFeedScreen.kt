package newsref.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.ImmutableList
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideBookColors
import newsref.app.blip.theme.ProvideSkyColors
import newsref.app.model.Source

@Composable
fun EventFeedScreen(
    route: EvemtFeedRoute,
    viewModel: EventFeedModel = viewModel { EventFeedModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val ruler = Blip.ruler

    Column(
        verticalArrangement = Blip.ruler.columnSpaced
    ) {
        BalloonChart(
            selectedId = state.selectedId ?: 0,
            points = state.balloonPoints,
            height = 400.dp,
            onClickCloud = viewModel::selectId
        )

        val height = 130
        CardFeed(
            selectedId = state.selectedId,
            items = state.chapterPacks,
            onSelect = viewModel::selectId,
            getId = { it.chapter.id }
        ) { pack, isSelected ->
            EventCard(
                modifier = Modifier.fillMaxWidth()
                    .height(height.dp)
            ) {
                Row(
                    horizontalArrangement = ruler.rowSpaced
                ) {
                    val color = Blip.colors.getSwatchFromIndex(pack.chapter.id)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxHeight()
                            .aspectRatio(1f)
                    ) {
                        val balloonSize = maxOf(pack.chapter.score / 50f * height, 32f)
                        Box(
                            modifier = Modifier.size(balloonSize.dp)
                                .circleIndicator(isSelected) {
                                    drawBalloon(color)
                                }
                                .clip(ruler.round)
                                .clickable { viewModel.selectId(pack.chapter.id) }
                        )
                        ProvideSkyColors {
                            H2(pack.chapter.score.toString())
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                            .fillMaxHeight()
                    ) {
                        Box(
                            contentAlignment = Alignment.BottomStart,
                            modifier = Modifier.heightIn(min = 36.dp)
                        ) {
                            H2(
                                text = pack.chapter.title ?: "null",
                                maxLines = 2,
                            )
                        }
                        val source = pack.sources.first()
                        Text(source.title ?: "null", maxLines = 1)
                        Spacer(modifier = Modifier.weight(1f))
                        SourceArray(pack.sources, color)
                    }
                    val imgUrl = pack.sources.firstOrNull { it.imageUrl != null }?.imageUrl
                    imgUrl?.let {

                        Box(
                            modifier = Modifier.fillMaxHeight()
                                .aspectRatio(1f)
                                .shadow(ruler.shadowElevation, ruler.round)
                                .background(color)
                                .padding(ruler.innerPadding)
                        ) {
                            AsyncImage(
                                model = it,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(ruler.round)
                            )
                        }
                    }
                }
            }
        }
    }
}