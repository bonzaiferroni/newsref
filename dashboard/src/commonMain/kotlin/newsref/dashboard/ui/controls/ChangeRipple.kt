package newsref.dashboard.ui.controls

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeRipple(
    rippleConfiguration: RippleConfiguration = brighterRipple,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalRippleConfiguration provides rippleConfiguration) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private val brighterRipple = RippleConfiguration(
    color = Color.White,
    rippleAlpha = RippleAlpha(
        hoveredAlpha = .2f,
        pressedAlpha = .3f,
        focusedAlpha = .1f,
        draggedAlpha = .3f,
    )
)