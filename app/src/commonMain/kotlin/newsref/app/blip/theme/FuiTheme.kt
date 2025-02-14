package newsref.app.blip.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

interface FuiTheme {
    val layout: FuiLayout
    val colors: FuiColors
}

interface FuiColors {
    val primary: Color
}

interface FuiLayout {
    val spacing: Int

    val baseSpacing: Dp get() = spacing.dp
    val halfSpacing: Dp get() = (spacing / 2).dp
    val basePadding: PaddingValues get() = PaddingValues(baseSpacing)
    val halfPadding: PaddingValues get() = PaddingValues(halfSpacing)
}

val LocalTheme = staticCompositionLocalOf<FuiTheme> {
    error("No Nav provided")
}

object Fui {
    val colors: FuiColors @Composable @ReadOnlyComposable get() = LocalTheme.current.colors
    val layout: FuiLayout @Composable @ReadOnlyComposable get() = LocalTheme.current.layout
}

@Composable
fun ProvideTheme(
    theme: FuiTheme = DefaultTheme,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalTheme provides theme) {
        content()
    }
}