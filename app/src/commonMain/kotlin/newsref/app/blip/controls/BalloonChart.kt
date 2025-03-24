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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import newsref.app.blip.theme.Blip
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@Composable
fun BalloonChart(
    selectedId: Long,
    balloons: BalloonsData?,
    height: Dp,
    onClickBalloon: (Long) -> Unit,
) {
    var size by remember { mutableStateOf(DpSize.Zero) }
    val ruler = Blip.ruler

    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .clipToBounds()
            .onGloballyPositioned { coordinates ->
                size = with(density) { coordinates.size.toSize().toDpSize() }
            }
    ) {
        if (balloons == null || balloons.points.isEmpty() || size == DpSize.Zero) return
        val space = remember(balloons) {
            generateBalloonSpace(balloons)
        }
        val points = balloons.points
        val xTicks = balloons.xTicks
        val bottomMargin = 30
        val chartHeight = size.height.value - bottomMargin
        val yScale = chartHeight / space.yRange
        val xScale = size.width.value / space.xRange
        // val sizeScale = size.height / (space.sizeMax * 4)

        if (xTicks != null) {
            AxisTickBox(xTicks, xScale, space)
        }

        for (point in points) {
            val isSelected = point.id == selectedId
            var color = Blip.colors.getSwatchFromIndex(point.colorIndex)
            val interactionSource = remember { MutableInteractionSource() }
            val isHovered = interactionSource.collectIsHoveredAsState().value
            val bgColor = when (isHovered) {
                true -> color
                false -> color.copy(alpha = .75f)
            }
            val radius = maxOf((point.size * yScale / space.sizeScale) / 2, BALLOON_MIN_SIZE)
            val x = (point.x - space.xMin) * xScale
            val y = maxOf(((point.y - space.yMin) * yScale), radius)
            val center = Offset(x, chartHeight - y)
            val transition = rememberInfiniteTransition()
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
                    .clickable { onClickBalloon(point.id) }
            ) {
                point.imageUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(ruler.round)
                            .alpha(.5f)
                    )
                }
            }
        }
    }
}

@Composable
internal fun AxisTickBox(xTicks: ImmutableList<AxisTick>, xScale: Float, space: BalloonSpace) {
    val textMeasurer = rememberTextMeasurer()
    Box(
        modifier = Modifier.fillMaxSize()
            .drawBehind {
                for (tick in xTicks) {
                    val x = (tick.value - space.xMin) * xScale
                    drawLine(
                        color = Color.White.copy(.2f),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 5f
                    )
                    val textLayoutResult = textMeasurer.measure(tick.label)
                    val y = size.height - textLayoutResult.size.height
                    drawText(textLayoutResult, Color.White.copy(.5f), Offset(x + 5, y))
                }
            }
    )
}

fun DrawScope.drawBalloon(color: Color) {
    val radius = size.minDimension / 2
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

private fun generateBalloonSpace(config: BalloonsData): BalloonSpace {
    val points = config.points
    val yMinInitial = points.minOf { it.y - it.size / 2 }
    val yMaxInitial = points.maxOf { it.y + it.size / 2 }
    val sizeMin = points.minOf { it.size }
    val sizeMax = points.maxOf { it.size }
    val sizeScale = sizeMax * 8 / (yMaxInitial - yMinInitial)
    val yMin = points.minOf { it.y - (it.size / 2) / sizeScale}
    val yMax = points.maxOf { it.y + (it.size / 2) / sizeScale}
    val xMin = config.xMin ?: points.minOf { it.x }
    val xMax = config.xMax ?: points.maxOf { it.x }
    return BalloonSpace(
        yMin = yMin,
        yMax = yMax,
        yRange = yMax - yMin,
        xMin = xMin,
        xMax = xMax,
        xRange = xMax - xMin,
        sizeMin = sizeMin,
        sizeMax = sizeMax,
        sizeScale = sizeScale,
    )
}

fun generateAxisTicks(earliest: Instant, latest: Instant = Clock.System.now()): ImmutableList<AxisTick> {
    val now = Clock.System.now()
    val span = latest - earliest
    val interval = when {
        span > 21.days -> 7.days
        span > 10.days -> 3.days
        span > 2.days -> 1.days
        else -> 6.hours
    }
    val tz = TimeZone.currentSystemDefault()
    val dayStart = earliest.toLocalDateTime(tz).date.atStartOfDayIn(tz)
    val timeStart = dayStart + interval * (((earliest - dayStart) / interval).toInt() + 1)
    val intervalCount = (span / interval).toInt()
    val currentYear = now.toLocalDateTime(tz).year
    return (0 until intervalCount).map { i ->
        val time = timeStart + interval * i
        val localTime = time.toLocalDateTime(tz)
        val year = localTime.year.toString()
        val date = "${localTime.monthNumber}/${localTime.dayOfMonth}"
        val day = localTime.dayOfWeek.toString().take(3)
        val label = when {
            localTime.year != currentYear -> "$day,\n$date, $year"
            time < now - 7.days -> "$day,\n$date"
            localTime.hour == 0 && localTime.minute == 0 -> day
            else -> "${localTime.hour}:${localTime.minute.toString().padStart(2, '0')}"
        }
        val x = (now - time).inWholeHours / 24f
        AxisTick(-x, label)
    }.toImmutableList()
}

data class BalloonsData(
    val points: ImmutableList<BalloonPoint> = persistentListOf(),
    val xTicks: ImmutableList<AxisTick>? = null,
    val xMax: Float? = null,
    val xMin: Float? = null,
)

data class BalloonPoint(
    val id: Long,
    val x: Float,
    val y: Float,
    val size: Float,
    val text: String,
    val colorIndex: Int,
    val imageUrl: String? = null,
)

data class AxisTick(
    val value: Float,
    val label: String,
)

internal data class BalloonSpace(
    val yMin: Float,
    val yMax: Float,
    val xMin: Float,
    val xMax: Float,
    val yRange: Float,
    val xRange: Float,
    val sizeMin: Float,
    val sizeMax: Float,
    val sizeScale: Float,
)

const val BALLOON_MIN_SIZE = 20f