package newsref.app.pond.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

interface PondTheme {
    val layout: PondRuler
    val colors: PondColors
    val skyColors: PondLocalColors
    val bookColors: PondLocalColors
    val typography: PondTypography
}

object Pond {
    val theme: PondTheme @Composable @ReadOnlyComposable get() = LocalTheme.current
    val colors: PondColors @Composable @ReadOnlyComposable get() = LocalTheme.current.colors
    val ruler: PondRuler @Composable @ReadOnlyComposable get() = LocalTheme.current.layout
    val localColors: PondLocalColors @Composable @ReadOnlyComposable get() = LocalColors.current
    val typo: PondTypography @Composable @ReadOnlyComposable get() = LocalTheme.current.typography
}

@Composable
fun ProvideTheme(
    theme: PondTheme = DefaultTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalTheme provides theme) {
        content()
    }
}

@Composable
fun ProvideBookColors(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalColors provides Pond.theme.bookColors) {
        content()
    }
}

@Composable
fun ProvideSkyColors(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalColors provides Pond.theme.skyColors) {
        content()
    }
}

val LocalTheme = staticCompositionLocalOf<PondTheme> {
    error("No theme provided")
}

val LocalColors = compositionLocalOf<PondLocalColors> {
    error("No mode colors provided")
}