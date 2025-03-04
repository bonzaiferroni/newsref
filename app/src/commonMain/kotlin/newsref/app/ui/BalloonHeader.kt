package newsref.app.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.ImmutableList
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideSkyColors
import newsref.app.model.SourceBit

@Composable
fun BalloonHeader(
    color: Color,
    title: String,
    imageUrl: String?,
    score: Int,
    height: Float,
    isSelected: Boolean,
    onSelect: () -> Unit,
    sources: ImmutableList<SourceBit>?
) {
    Row(
        horizontalArrangement = Blip.ruler.rowSpaced,
        modifier = Modifier.height(height.dp)
    ) {
        HeaderImage(color, imageUrl)

        HeaderTitle(title, sources, color)

        HeaderBalloon(score, isSelected, color, onSelect, height)
    }
}

@Composable
fun HeaderImage(color: Color, imageUrl: String?) {
    val ruler = Blip.ruler
    Box(
        modifier = Modifier.fillMaxHeight()
            .aspectRatio(1f)
            .shadow(ruler.shadowElevation, ruler.round)
            .background(color)
            .padding(ruler.innerPadding)
    ) {
        imageUrl?.let {
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

@Composable
fun RowScope.HeaderTitle(
    title: String,
    sources: ImmutableList<SourceBit>?,
    color: Color
) {
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
                text = title,
                maxLines = 2,
            )
        }
        if (sources != null) {
            val source = sources.first()
            Text("Next: ${source.title}", maxLines = 1)
            Spacer(modifier = Modifier.weight(1f))
            SourceArray(sources, color)
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