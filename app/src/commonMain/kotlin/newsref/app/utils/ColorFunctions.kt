package newsref.app.utils

import androidx.compose.ui.graphics.Color

fun Color.darken(amount: Float = 0.2f): Color {
    val r = (red * 255 - amount * 255).coerceIn(0f, 255f)
    val g = (green * 255 - amount * 255).coerceIn(0f, 255f)
    val b = (blue * 255 - amount * 255).coerceIn(0f, 255f)

    return Color(r / 255f, g / 255f, b / 255f, alpha)
}

fun Color.brighten(amount: Float = 0.2f) = darken(-amount)
