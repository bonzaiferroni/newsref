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
    val shine: Color
    val swatches: List<Color>
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
    override val surfaceBook = Color(0xFFdbdcdc)
    override val background = Color(0xFF1b7161)
    override val shine = Color(0xFFffe746)
    override val swatches = listOf(
        Color(0xFF18B199),
        Color(0xFF004587),
        Color(0xFFA11B0A),
        Color(0xFFE3A100),
        Color(0xFF6B3E26),
        Color(0xFFDC6A00),
        Color(0xFF7D3CCF),
        Color(0xFF00B8C4),
        Color(0xFF737373),
    )
}