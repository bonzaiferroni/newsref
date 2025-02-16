package newsref.app.blip.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

interface BlipTheme {
    val layout: BlipRuler
    val colors: BlipColors
    val skyColors: BlipLocalColors
    val bookColors: BlipLocalColors
    val typography: BlipTypography
}

object Blip {
    val theme: BlipTheme @Composable @ReadOnlyComposable get() = LocalTheme.current
    val colors: BlipColors @Composable @ReadOnlyComposable get() = LocalTheme.current.colors
    val ruler: BlipRuler @Composable @ReadOnlyComposable get() = LocalTheme.current.layout
    val localColors: BlipLocalColors @Composable @ReadOnlyComposable get() = LocalColors.current
    val typ: BlipTypography @Composable @ReadOnlyComposable get() = LocalTheme.current.typography
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
fun ProvideBookColors(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalColors provides Blip.theme.bookColors) {
        content()
    }
}

@Composable
fun ProvideSkyColors(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalColors provides Blip.theme.skyColors) {
        content()
    }
}

val LocalTheme = staticCompositionLocalOf<BlipTheme> {
    error("No theme provided")
}

val LocalColors = compositionLocalOf<BlipLocalColors> {
    error("No mode colors provided")
}