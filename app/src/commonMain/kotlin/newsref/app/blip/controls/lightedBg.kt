package newsref.app.blip.controls

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.adamglin.composeshadow.dropShadow

fun Modifier.lightedBg(
    color: Color,
    light: Color,
    angle: Float,
    shape: Shape
) = dropShadow(
    shape = shape,
    color = Color.Black.copy(.5f),
    offsetX = ((angle * 2 - 1) * 4).dp,
    offsetY = 4.dp,
    blur = 20.dp,
    spread = 5.dp,
)
    .clip(shape)
    .drawBehind {
        val gradient = Brush.linearGradient(
            colors = listOf(light.copy(.2f), Color.Transparent),
            start = Offset(angle * size.width, 0f),
            end = Offset((1 - angle) * size.width, size.height)
        )
        val borderColor = blendColors(light, color, .5f)
        val border = Brush.linearGradient(
            colors = listOf(borderColor, color),
            start = Offset(angle * size.width, 0f),
            end = Offset((1 - angle) * size.width, size.height)
        )
        val innerSize = Size(size.width - 2, size.height - 1)
        val innerOffset = Offset(1f, 1f)
        drawRect(border)
        drawRect(color = color, topLeft = innerOffset, size = innerSize)
        drawRect(brush = gradient, topLeft = innerOffset, size = innerSize)
    }

fun blendColors(color1: Color, color2: Color, alpha: Float): Color {
    val r = (alpha * color1.red + (1 - alpha) * color2.red).coerceIn(0f, 1f)
    val g = (alpha * color1.green + (1 - alpha) * color2.green).coerceIn(0f, 1f)
    val b = (alpha * color1.blue + (1 - alpha) * color2.blue).coerceIn(0f, 1f)
    val a = (alpha * color1.alpha + (1 - alpha) * color2.alpha).coerceIn(0f, 1f)

    return Color(r, g, b, a)
}