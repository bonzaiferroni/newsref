package newsref.app.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.Instant
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideSkyColors
import newsref.app.model.SourceBit
import newsref.model.utils.formatSpanLong

@Composable
fun BalloonHeader(
    color: Color,
    title: String,
    imageUrl: String?,
    score: Int,
    height: Float,
    isSelected: Boolean,
    onSelect: () -> Unit,
    storyCount: Int?,
    time: Instant,
    sources: ImmutableList<SourceBit>?
) {
    Row(
        horizontalArrangement = Blip.ruler.rowTight,
        modifier = Modifier.height(height.dp)
    ) {
        ShapeImage(
            color = color,
            url = imageUrl,
            modifier = Modifier.fillMaxHeight()
        )

        HeaderMiddle(title, sources, color, time, storyCount)

        // HeaderBalloon(score, isSelected, color, onSelect, height)
    }
}

@Composable
fun ShapeImage(
    url: String?,
    color: Color,
    padding: PaddingValues = Blip.ruler.innerPadding,
    shape: Shape = Blip.ruler.round,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.aspectRatio(1f)
            .shadow(Blip.ruler.shadowElevation, shape)
            .background(color)
            .padding(padding)
    ) {
        url?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(shape)
            )
        }
    }
}

@Composable
fun RowScope.HeaderMiddle(
    title: String,
    sources: ImmutableList<SourceBit>?,
    color: Color,
    time: Instant,
    storyCount: Int?,
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.weight(1f)
            .fillMaxHeight()
    ) {
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier.heightIn(min = 32.dp)
        ) {
            H2(
                text = title,
                maxLines = 2,
            )
        }
        Row(
            horizontalArrangement = Blip.ruler.rowSpaced,
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxHeight()
            ) {
                storyCount?.let { Text("$it sources") }
                Text(time.formatSpanLong())
            }
            if (sources != null) {
                SourceArray(sources, color)
            }
        }
    }
}

@Composable
fun HeaderBalloon(
    score: Int,
    isSelected: Boolean,
    color: Color,
    onSelect: () -> Unit,
    height: Float
) {
    val ruler = Blip.ruler
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxHeight()
            .aspectRatio(1f)
    ) {
        val balloonSize = maxOf(score / 50f, .25f) * height
        Box(
            modifier = Modifier.size(balloonSize.dp)
                .circleIndicator(isSelected) {
                    drawBalloon(color)
                }
                .clip(ruler.round)
                .clickable(onClick = onSelect)
        )
        ProvideSkyColors {
            H2(score.toString())
        }
    }
}
