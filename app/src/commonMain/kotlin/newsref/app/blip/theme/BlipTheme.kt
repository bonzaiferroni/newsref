package newsref.app.blip.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

interface BlipTheme {
    val layout: BlipRuler
    val colors: BlipColors
}

val LocalTheme = staticCompositionLocalOf<BlipTheme> {
    error("No theme provided")
}

object Blip {
    val colors: BlipColors @Composable @ReadOnlyComposable get() = LocalTheme.current.colors
    val ruler: BlipRuler @Composable @ReadOnlyComposable get() = LocalTheme.current.layout
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