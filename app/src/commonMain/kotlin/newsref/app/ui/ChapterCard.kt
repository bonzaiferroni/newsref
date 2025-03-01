package newsref.app.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideSkyColors
import newsref.app.model.ChapterPack

@Composable
fun ChapterCard(
    pack: ChapterPack,
    height: Float,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onClick: () -> Unit,
) {
    val ruler = Blip.ruler

    Card(
        onClick = onClick,
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
                        .clickable(onClick = onSelect)
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