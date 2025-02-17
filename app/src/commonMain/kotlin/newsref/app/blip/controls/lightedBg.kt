package newsref.app.blip.controls

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.DefaultColors.shine

fun Modifier.lightedBg(
    color: Color,
    light: Color,
    shape: Shape,
    angle: Float
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
        val borderColor = blendColors(light, color, .5f)
        val border = Brush.linearGradient(
            colors = listOf(borderColor, color, color, color),
            start = Offset(angle * size.width, 0f),
            end = Offset((1 - angle) * size.width, size.height)
        )
        val innerSize = Size(size.width - 2, size.height - 1)
        val innerOffset = Offset(1f, 1f)
        drawRect(border)
        drawRect(color = color, topLeft = innerOffset, size = innerSize)
        val gradient = Brush.linearGradient(
            colors = listOf(light.copy(.1f), light.copy(.05f)),
            start = Offset(angle * size.width, 0f),
            end = Offset((1 - angle) * size.width, size.height)
        )
        drawRect(brush = gradient, topLeft = innerOffset, size = innerSize)
    }

@Composable
fun Modifier.lightedBg(
    color: Color,
    shape: Shape,
): Modifier {
    val angle = remember { angleFromClock() }
    return lightedBg(
        color,
        Blip.colors.shine,
        shape,
        angle
    )
}

fun blendColors(color1: Color, color2: Color, alpha: Float): Color {
    val r = (alpha * color1.red + (1 - alpha) * color2.red).coerceIn(0f, 1f)
    val g = (alpha * color1.green + (1 - alpha) * color2.green).coerceIn(0f, 1f)
    val b = (alpha * color1.blue + (1 - alpha) * color2.blue).coerceIn(0f, 1f)
    val a = (alpha * color1.alpha + (1 - alpha) * color2.alpha).coerceIn(0f, 1f)

    return Color(r, g, b, a)
}

fun angleFromClock() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    .hour / 24f