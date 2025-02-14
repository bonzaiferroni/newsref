package newsref.app.blip.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

interface BlipTheme {
    val layout: BlipLayout
    val colors: BlipColors
}

interface BlipColors {
    val primary: Color
    val content: Color
    val background: Color
    val surface: Color
}

interface BlipLayout {
    val spacing: Int

    val baseSpacing: Dp get() = spacing.dp
    val halfSpacing: Dp get() = (spacing / 2).dp
    val basePadding: PaddingValues get() = PaddingValues(baseSpacing)
    val halfPadding: PaddingValues get() = PaddingValues(halfSpacing)

    val rowGrouped: Arrangement.Horizontal get() = Arrangement.spacedBy(halfSpacing)
    val rowSpaced: Arrangement.Horizontal get() = Arrangement.spacedBy(baseSpacing)
    val columnGrouped: Arrangement.Vertical get() = Arrangement.spacedBy(halfSpacing)
    val columnSpaced: Arrangement.Vertical get() = Arrangement.spacedBy(baseSpacing)
}

val LocalTheme = staticCompositionLocalOf<BlipTheme> {
    error("No Nav provided")
}

object Blip {
    val colors: BlipColors @Composable @ReadOnlyComposable get() = LocalTheme.current.colors
    val layout: BlipLayout @Composable @ReadOnlyComposable get() = LocalTheme.current.layout
}

@Composable
fun ProvideTheme(
    theme: BlipTheme = DefaultTheme,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalTheme provides theme) {
        content()
    }
}