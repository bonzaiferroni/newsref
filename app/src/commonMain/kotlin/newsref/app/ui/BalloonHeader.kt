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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideSkyColors
import newsref.app.model.SourceBit
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

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
        HeaderImage(color, imageUrl)

        HeaderMiddle(title, sources, color, time, storyCount)

        // HeaderBalloon(score, isSelected, color, onSelect, height)
    }
}

@Composable
fun HeaderImage(
    color: Color,
    imageUrl: String?,
    padding: PaddingValues = Blip.ruler.innerPadding
) {
    Box(
        modifier = Modifier.fillMaxHeight()
            .aspectRatio(1f)
            .shadow(Blip.ruler.shadowElevation, Blip.ruler.round)
            .background(color)
            .padding(padding)
    ) {
        imageUrl?.let {
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
                Text("${time.agoLongFormat()} ago")
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

fun Instant.agoLongFormat() = (Clock.System.now() - this).let {
    when {
        it > 365.days -> "${it.inWholeDays / 365} years"
        it > 2.days -> "${it.inWholeDays} days"
        it > 1.days -> "1 day"
        it > 2.hours -> "${it.inWholeHours} hours"
        it > 1.hours -> "1 hour"
        it > 2.minutes -> "${it.inWholeMinutes} minutes"
        it > 1.minutes -> "1 minute"
        else -> "${it.inWholeSeconds} seconds"
    }
}