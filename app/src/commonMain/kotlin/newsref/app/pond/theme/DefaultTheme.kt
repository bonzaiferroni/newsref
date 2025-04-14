package newsref.app.pond.theme

import androidx.compose.runtime.Composable
import newsref.app.utils.darken

@Composable
fun DefaultTheme() = object : PondTheme {
    override val layout: PondRuler = DefaultRuler

    override val colors: PondColors = DefaultColors

    override val skyColors = PondLocalColors(
        mode = ColorMode.Sky,
        content = colors.contentSky,
        surface = colors.surfaceSky,
        highlight = colors.shine.darken(-.4f)
    )

    override val bookColors = PondLocalColors(
        mode = ColorMode.Book,
        content = colors.contentBook,
        surface = colors.surfaceBook,
        highlight = colors.shine.darken(.4f)
    )

    override val typography = DefaultTypography()
}