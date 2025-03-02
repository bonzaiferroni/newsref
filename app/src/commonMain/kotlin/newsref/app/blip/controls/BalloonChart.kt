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
    config: BalloonConfig,
    height: Dp,
    onClickCloud: (Long) -> Unit,
) {
    val points = config.points
    if (points.isEmpty()) return

    val space = remember(config) {
        generateBalloonSpace(config)
    }

    // CloudCanvas(points, space, height)
    BalloonBox(selectedId, config, space, height, onClickCloud)
}

private fun generateBalloonSpace(config: BalloonConfig): BalloonSpace {
    val points = config.points
    val yMinInitial = points.minOf { it.y - it.size / 2 }
    val yMaxInitial = points.maxOf { it.y + it.size / 2 }
    val sizeMin = points.minOf { it.size }
    val sizeMax = points.maxOf { it.size }
    val sizeScale = sizeMax * 4 / (yMaxInitial - yMinInitial)
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

data class BalloonConfig(
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

const val BALLOON_MIN_SIZE = 10f