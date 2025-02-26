package newsref.app.blip.controls

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

fun Modifier.bg(
    color: Color,
    shape: Shape,
) = shadow(8.dp, shape)
    .drawBehind {
        drawRect(color)
    }