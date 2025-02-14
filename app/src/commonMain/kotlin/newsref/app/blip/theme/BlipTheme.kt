package newsref.app.blip.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

interface BlipTheme {
    val layout: BlipRuler
    val colors: BlipColors
    val skyColors: ModeColors
    val bookColors: ModeColors
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