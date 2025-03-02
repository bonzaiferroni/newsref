@file:Suppress("DuplicatedCode")

package newsref.app.blip.controls

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.ImmutableList
import newsref.app.blip.theme.Blip

@Composable
internal fun BalloonBox(
    selected: Long,
    points: ImmutableList<BalloonPoint>,
    space: BalloonSpace,
    height: Dp,
    onClick: (Long) -> Unit,
) {
    var size by remember { mutableStateOf(Size.Zero) }
    val ruler = Blip.ruler

    Box (
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .clipToBounds()
            .onGloballyPositioned { coordinates ->
                size = coordinates.size.toSize() // Capture the box size
            }
    ) {
        if (size == Size.Zero) return
        val yScale = size.height / space.yRange
        val xScale = size.width / space.xRange
        // val sizeScale = size.height / (space.sizeMax * 4)

        for (point in points) {
            val isSelected = point.id == selected

            var color = Blip.colors.getSwatchFromIndex(point.colorIndex)
            val interactionSource = remember { MutableInteractionSource() }
            val isHovered = interactionSource.collectIsHoveredAsState().value
            val bgColor = when(isHovered) {
                true -> color
                false -> color.copy(alpha = .75f)
            }
            val radius = maxOf((point.size * yScale / space.sizeScale) / 2, BALLOON_MIN_SIZE)
            val x = (point.x - space.xMin) * xScale
            val y = maxOf(((point.y - space.yMin) * yScale), radius)
            val center = Offset(x, size.height - y)
            val transition = rememberInfiniteTransition()
            val floatDistance = (point.size * .75).toInt()
            val initialValue = remember { (-10..10).random().toFloat() }
            val targetValue = remember { (-10..10).random().toFloat() }
            val duration = remember { (4000..6000).random() }
            val offsetY by transition.animateFloat(
                initialValue = initialValue,
                targetValue = targetValue,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = duration, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Box(
                modifier = Modifier.size((radius * 2).dp)
                    .offset((center.x - radius).dp, (center.y - radius).dp)
                    .graphicsLayer { translationY = offsetY }
                    .circleIndicator(isSelected) {
                        drawBalloon(bgColor)
                    }
                    .clip(CircleShape)
                    .hoverable(interactionSource)
                    .clickable { onClick(point.id) }
            ) {
                point.imageUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(ruler.round)
                            .alpha(.5f)
                    )
                }
            }
        }
    }
}

fun DrawScope.drawBalloon(color: Color) {
    val radius = size.minDimension / 2
    drawCircle(
        color = color.copy(.75f),
        radius = radius
    )
//    val lightColor = Color.White
//    val lightOffset = Offset(center.x - radius * 0.3f, center.y - radius * 0.3f)
//    drawCircle(
//        brush = Brush.radialGradient(
//            colors = listOf(
//                lightColor.copy(alpha = 0.05f),
//                lightColor.copy(alpha = 0.05f),
//                lightColor.copy(alpha = 0.02f),
//                lightColor.copy(alpha = 0f),
//            ),
//            center = lightOffset,
//            radius = radius
//        ),
//        radius = radius
//    )
    drawCircle(
        color = color,
        radius = radius,
        style = Stroke(width = 2.dp.toPx()) // Stroke style
    )
}

data class LightDirection(
    val x: Float,
    val y: Float
)