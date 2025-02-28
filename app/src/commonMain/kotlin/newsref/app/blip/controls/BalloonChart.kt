@file:Suppress("DuplicatedCode")

package newsref.app.blip.controls

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlinx.collections.immutable.ImmutableList

@Composable
fun BalloonChart(
    selected: Long,
    points: ImmutableList<BalloonPoint>,
    height: Dp,
    onClickCloud: (Long) -> Unit,
) {
    if (points.isEmpty()) return

    val space = remember(points) {
        generateBalloonSpace(points)
    }

    // CloudCanvas(points, space, height)
    BalloonBox(selected, points, space, height, onClickCloud)
}

private fun generateBalloonSpace(points: List<BalloonPoint>): BalloonSpace {
    val yMinInitial = points.minOf { it.y - it.size / 2 }
    val yMaxInitial = points.maxOf { it.y + it.size / 2 }
    val xMin = points.minOf { it.x }
    val xMax = points.maxOf { it.x }
    val sizeMin = points.minOf { it.size }
    val sizeMax = points.maxOf { it.size }
    val sizeScale = sizeMax * 4 / (yMaxInitial - yMinInitial)
    val yMin = points.minOf { it.y - (it.size / 2) / sizeScale}
    val yMax = points.maxOf { it.y + (it.size / 2) / sizeScale}
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

data class BalloonPoint(
    val id: Long,
    val x: Float,
    val y: Float,
    val size: Float,
    val text: String,
    val colorIndex: Int
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