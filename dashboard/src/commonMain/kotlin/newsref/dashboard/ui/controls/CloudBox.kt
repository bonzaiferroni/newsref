package newsref.dashboard.ui.controls

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.collections.immutable.ImmutableList
import newsref.dashboard.utils.SetToolTip
import newsref.dashboard.utils.TipType
import newsref.dashboard.utils.ToolTip
import kotlin.math.roundToInt

@Composable
internal fun CloudBox(
    points: ImmutableList<CloudPoint>,
    space: CloudSpace,
    height: Dp,
    onClick: (Long) -> Unit,
) {
    var size by remember { mutableStateOf(Size.Zero) }

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
        val sizeScale = (size.height / 4) / maxOf(space.sizeRange, CLOUD_MIN_SIZE)

        var index = 0
        for (point in points) {
            var color = cloudColors[index++ % cloudColors.size]
            val interactionSource = remember { MutableInteractionSource() }
            val isHovered = interactionSource.collectIsHoveredAsState().value
            SetToolTip(ToolTip(point.text, TipType.Action), interactionSource)
            val bgColor = when(isHovered) {
                true -> color
                false -> color.copy(alpha = .75f)
            }
            val center = Offset((point.x - space.xMin) * xScale, size.height - ((point.y - space.yMin) * yScale) - 20)
            val radius = maxOf(point.size * sizeScale, CLOUD_MIN_SIZE)
            val transition = rememberInfiniteTransition()
            val initialValue = remember { (-5..5).random().toFloat() }
            val targetValue = remember { (-5..5).random().toFloat() }
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
                    .clip(CircleShape)
                    .background(bgColor)
                    .border(2.dp, color, CircleShape)
                    .hoverable(interactionSource)
                    .clickable { onClick(point.id) }
            )
        }
    }
}

@Composable
internal fun BoxCircle(
    color: Color,
    center: Offset,
    size: Float,
) {
    Box(
        modifier = Modifier.size(size.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = .75f))
            .offset { IntOffset(center.x.roundToInt(), center.y.roundToInt()) },
    )
}