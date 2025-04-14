package newsref.app.blip.controls

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import newsref.app.blip.behavior.animate
import newsref.app.blip.behavior.animateFloat
import newsref.app.blip.theme.Blip

@Composable
fun ButtonToggle(
    value: Boolean,
    onToggle: (Boolean) -> Unit,
    color : Color = Blip.colors.primary,
    content: @Composable () -> Unit,
) {
    val outlineColor = Blip.localColors.content.copy(alpha = .5f)
    val scale by animateFloat(if (value) 1f else 0f)
    val shape = Blip.ruler.round
    val density = LocalDensity.current
    Box(
        modifier = Modifier
            .clip(shape)
            .clickable { onToggle(!value) }
            .drawBehind {
                val outline = shape.createOutline(size, layoutDirection = LayoutDirection.Ltr, density = density)
                drawOutline(
                    outline = outline,
                    color = outlineColor,
                    style = Stroke(width = 4.dp.toPx()) // 4.dp wide stroke
                )
                withTransform({
                    scale(scale, scale, pivot = center) // Scale from the center
                }) {
                    drawRoundRect(
                        color = color,
                        cornerRadius = CornerRadius(16.dp.toPx()),
                    )
                }
            }
            .padding(Blip.ruler.halfPadding)
    ) {
        content()
    }
}

@Composable
fun ButtonToggle(
    value: Boolean,
    text: String,
    onToggle: (Boolean) -> Unit,
    color : Color = Blip.colors.primary,
) {
    ButtonToggle(value, onToggle, color) {
        val color = when (value) {
            true -> Blip.colors.contentSky
            false -> Blip.localColors.content
        }.animate()
        Text(text = text, color = color)
    }
}