package newsref.app.fui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import newsref.app.core.Nav

interface FuiTheme {
    val spacing: Int

    val baseSpacing: Dp get() = spacing.dp
    val halfSpacing: Dp get() = (spacing / 2).dp
    val basePadding: PaddingValues get() = PaddingValues(baseSpacing)
    val halfPadding: PaddingValues get() = PaddingValues(halfSpacing)
}

object DefaultTheme : FuiTheme {
    override val spacing: Int = 16
}

val LocalTheme = staticCompositionLocalOf<FuiTheme> {
    error("No Nav provided")
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