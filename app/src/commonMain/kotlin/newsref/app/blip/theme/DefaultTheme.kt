package newsref.app.blip.theme

object DefaultTheme : BlipTheme {
    override val layout: BlipRuler = object : BlipRuler{
        override val spacing: Int = 16
        override val corner: Int = 16
    }

    override val colors: BlipColors = DefaultColors

    override val skyColors = BlipLocalColors(
        mode = ColorMode.Sky,
        content = colors.contentSky,
        surface = colors.surfaceSky,
    )

    override val bookColors = BlipLocalColors(
        mode = ColorMode.Book,
        content = colors.contentBook,
        surface = colors.surfaceBook,
    )

    override val typography = DefaultTypography
}