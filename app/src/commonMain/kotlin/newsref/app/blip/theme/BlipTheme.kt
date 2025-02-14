package newsref.app.blip.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.L
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

interface BlipTheme {
    val layout: BlipRuler
    val colors: BlipColors
    val lightColors: ModeColors
    val darkColors: ModeColors
}

object Blip {
    val theme: BlipTheme @Composable @ReadOnlyComposable get() = LocalTheme.current
    val ruler: BlipRuler @Composable @ReadOnlyComposable get() = LocalTheme.current.layout
    val colors: ModeColors @Composable @ReadOnlyComposable get() = LocalColors.current
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

@Composable
fun ProvideColors(
    colors: ModeColors,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalColors provides colors) {
        content()
    }
}

val LocalTheme = staticCompositionLocalOf<BlipTheme> {
    error("No theme provided")
}

val LocalColors = compositionLocalOf<ModeColors> {
    error("No mode colors provided")
}