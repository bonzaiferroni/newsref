package newsref.app.blip.controls

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import newsref.app.blip.theme.Blip

@Composable
fun Modifier.circleIndicator(isVisible: Boolean, block: DrawScope.() -> Unit): Modifier {
    if (!isVisible) return this.drawBehind { block() }
    val color = Blip.localColors.content
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )
    return this.drawBehind {
        this.block()
        drawCircle(
            color = color.copy(.5f),
            radius = size.width / 2f + 5f,
            style = Stroke(
                width = 3.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f, 3f), phase) // Dash pattern
            )
        )
    }
}