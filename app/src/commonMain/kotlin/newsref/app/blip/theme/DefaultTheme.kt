package newsref.app.blip.theme

import androidx.compose.ui.graphics.Color

object DefaultTheme : BlipTheme {
    override val layout: BlipRuler = object : BlipRuler{
        override val spacing: Int = 16
        override val corner: Int = 12
    }

    override val colors: BlipColors = object : BlipColors{
        override val primary: Color = Color(0xFF57965c)
        override val contentDark: Color = Color(0xFFf5f6f6)
        override val surfaceDark: Color = Color(0xFF1e1f22)
        override val contentLight: Color = Color(0xFF050606)
        override val surfaceLight: Color = Color(0xFFdcdcdc)
        override val background: Color = Color(0xFF1b7161)
    }

    override val lightColors = ModeColors(
        mode = ColorMode.Light,
        primary = colors.primary,
        content = colors.contentLight,
        surface = colors.surfaceLight,
        background = colors.background
    )

    override val darkColors = ModeColors(
        mode = ColorMode.Dark,
        primary = colors.primary,
        content = colors.contentDark,
        surface = colors.surfaceDark,
        background = colors.background
    )
}