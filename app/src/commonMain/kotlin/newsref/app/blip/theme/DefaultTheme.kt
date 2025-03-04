package newsref.app.blip.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun DefaultTheme() = object : BlipTheme {
    override val layout: BlipRuler = DefaultRuler

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

    override val typography = DefaultTypography()
}