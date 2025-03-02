@file:Suppress("DuplicatedCode")

package newsref.app.blip.controls

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun BalloonChart(
    selectedId: Long,
    points: ImmutableList<BalloonPoint>,
    height: Dp,
    onClickCloud: (Long) -> Unit,
    xTicks: ImmutableList<AxisTick>? = null
) {
    if (points.isEmpty()) return

    val space = remember(points) {
        generateBalloonSpace(points)
    }

    // CloudCanvas(points, space, height)
    BalloonBox(selectedId, points, space, height, onClickCloud, xTicks)
}

private fun generateBalloonSpace(points: List<BalloonPoint>): BalloonSpace {
    val yMinInitial = points.minOf { it.y - it.size / 2 }
    val yMaxInitial = points.maxOf { it.y + it.size / 2 }
    val sizeMin = points.minOf { it.size }
    val sizeMax = points.maxOf { it.size }
    val sizeScale = sizeMax * 4 / (yMaxInitial - yMinInitial)
    val yMin = points.minOf { it.y - (it.size / 2) / sizeScale}
    val yMax = points.maxOf { it.y + (it.size / 2) / sizeScale}
    val xMin = points.minOf { it.x }
    val xMax = points.maxOf { it.x }
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

const val BALLOON_MIN_SIZE = 10f