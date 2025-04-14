package newsref.app.pond.controls

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import newsref.app.pond.theme.Pond

@Composable
fun Modifier.circleIndicator(isVisible: Boolean, block: DrawScope.() -> Unit): Modifier {
    if (!isVisible) return this.drawBehind { block() }
    val color = Pond.localColors.content
    val density = LocalDensity.current
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 50f * density.density,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )
    val dotSize = 3f * density.density
    return this.drawBehind {
        this.block()
        drawCircle(
            color = color.copy(.5f),
            radius = size.width / 2f + 4f * density.density,
            style = Stroke(
                width = dotSize,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(dotSize, dotSize), phase) // Dash pattern
            )
        )
    }
}