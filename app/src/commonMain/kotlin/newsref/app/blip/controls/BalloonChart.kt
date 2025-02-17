package newsref.app.blip.controls

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlinx.collections.immutable.ImmutableList

@Composable
fun BalloonChart(
    points: ImmutableList<BalloonPoint>,
    height: Dp,
    onClickCloud: (Long) -> Unit,
) {
    if (points.isEmpty()) return

    val space = remember(points) {
        generateBalloonSpace(points)
    }

    // CloudCanvas(points, space, height)
    BalloonBox(points, space, height, onClickCloud)
}

private fun generateBalloonSpace(points: List<BalloonPoint>): BalloonSpace {
    val yMin = points.minOf { it.y - it.size }
    val yMax = points.maxOf { it.y + it.size }
    val xMin = points.minOf { it.x }
    val xMax = points.maxOf { it.x }
    val sizeMin = points.minOf { it.size }
    val sizeMax = points.maxOf { it.size }
    return BalloonSpace(
        yMin = yMin,
        yMax = yMax,
        yRange = yMax - yMin,
        xMin = xMin,
        xMax = xMax,
        xRange = xMax - xMin,
        sizeMin = sizeMin,
        sizeMax = sizeMax,
        sizeRange = sizeMax - sizeMin,
    )
}

data class BalloonPoint(
    val id: Long,
    val x: Float,
    val y: Float,
    val size: Float,
    val text: String,
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
    val sizeRange: Float,
)

internal val balloonColors = listOf(
    Color(0xFF18B199),
    Color(0xFF004587),
    Color(0xFFA11B0A),
    Color(0xFFE3A100),
    Color(0xFF6B3E26),
    Color(0xFFDC6A00),
    Color(0xFF7D3CCF),
    Color(0xFF00B8C4),
    Color(0xFF737373),
)

const val BALLOON_MIN_SIZE = 10f