package newsref.app.blip.controls

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.circleIndicator(isVisible: Boolean, color: Color, block: DrawScope.() -> Unit) = this.drawBehind {
    this.block()
    if (isVisible) {
        drawCircle(
            color = color.copy(.5f),
            radius = size.width / 2f + 4f,
            style = Stroke(width = 2.dp.toPx()) // Stroke style
        )
    }
}