package newsref.dashboard.ui.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun CloudCanvas(points: ImmutableList<CloudPoint>, space: CloudSpace, height: Dp) {
    Canvas(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .clipToBounds()
    ) {
        val yScale = size.height / space.yRange
        val xScale = size.width / space.xRange
        val sizeScale = size.height / (space.sizeMax * 4)

        var index = 0
        for (point in points) {
            var color = cloudColors[index++ % cloudColors.size]
            val center = Offset((point.x - space.xMin) * xScale, size.height - ((point.y - space.yMin) * yScale + 20))
            val radius = 10 + point.size * sizeScale
            drawCircle(
                color = color.copy(alpha = .75f),
                radius = radius,
                center = center
            )
            drawCircle(
                color = color,
                radius = radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}