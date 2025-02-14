package newsref.app.blip.theme

import androidx.compose.ui.graphics.Color

object DefaultTheme : BlipTheme {
    override val layout: BlipRuler = object : BlipRuler{
        override val spacing: Int = 16
        override val corner: Int = 12
    }

    override val colors: BlipColors = object : BlipColors{
        override val primary: Color = Color(0xFF57965c)
        override val contentSky: Color = Color(0xFFf5f6f6)
        override val surfaceSky: Color = Color(0xFF1b7161)
        override val contentBook: Color = Color(0xFF050606)
        override val surfaceBook: Color = Color(0xFFdcdcdc)
    }

    override val skyColors = ModeColors(
        mode = ColorMode.Sky,
        primary = colors.primary,
        content = colors.contentSky,
        surface = colors.surfaceSky,
    )

    override val bookColors = ModeColors(
        mode = ColorMode.Book,
        primary = colors.primary,
        content = colors.contentBook,
        surface = colors.surfaceBook,
    )
}