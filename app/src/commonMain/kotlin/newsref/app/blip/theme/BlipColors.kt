package newsref.app.blip.theme

import androidx.compose.ui.graphics.Color

interface BlipColors {
    val primary: Color
    val accent: Color
    val contentSky: Color
    val surfaceSky: Color
    val contentBook: Color
    val surfaceBook: Color
    val background: Color
}

enum class ColorMode {
    Sky,
    Book,
}

data class BlipLocalColors(
    val mode: ColorMode,
    val content: Color,
    val surface: Color,
)

object DefaultColors : BlipColors{
    override val primary = Color(0xFF57965c)
    override val accent = Color(0xFFffe746)
    override val contentSky = Color(0xFFf5f6f6)
    override val surfaceSky = Color.Transparent
    override val contentBook = Color(0xFF1d190e)
    override val surfaceBook = Color(0xFFddd9c9)
    override val background = Color(0xFF1b7161)
}