package newsref.app.blip.theme

import androidx.compose.ui.graphics.Color

interface BlipColors {
    val primary: Color
    val accent: Color
    val contentSky: Color
    val surfaceSky: Color
    val contentBook: Color
    val surfaceBook: Color
}

enum class ColorMode {
    Sky,
    Book,
}

data class ModeColors(
    val mode: ColorMode,
    val primary: Color,
    val accent: Color,
    val content: Color,
    val surface: Color,
)