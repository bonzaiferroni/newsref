package newsref.dashboard.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.dashboard.halfPadding
import newsref.dashboard.halfSpacing
import newsref.dashboard.ui.screens.ScreenModel

@Composable
fun ToolTipper(
    viewModel: ToolTipperModel = viewModel { ToolTipperModel() },
    content: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(state.isVisible) {
        if (state.isVisible) {
            alpha.animateTo(1f, animationSpec = tween(200, easing = EaseOut))
        } else {
            alpha.animateTo(0f, animationSpec = tween(200, easing = EaseOut))
        }
    }

    Box {
        CompositionLocalProvider(LocalToolTipper provides viewModel) {
            content()
        }
        val backgroundColor = MaterialTheme.colorScheme.surfaceDim
        val contentColor = MaterialTheme.colorScheme.onSurface
        Surface(
            color = backgroundColor,
            contentColor = contentColor,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(alpha.value)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(halfPadding)
            ) {
                Text(state.tooltip)
            }
        }
    }
}

class ToolTipperModel : ScreenModel<ToolTipperState>(ToolTipperState()) {
    fun setTip(tip: String) {
        setState { it.copy(tooltip = tip, isVisible = true) }
    }

    fun releaseTip(tip: String) {
        if (stateNow.tooltip != tip) return
        setState { it.copy(isVisible = false) }
    }
}

@Composable
fun SetToolTip(text: String): MutableInteractionSource {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val toolTipper = LocalToolTipper.current
    if (isHovered) {
        toolTipper.setTip(text)
    } else {
        toolTipper.releaseTip(text)
    }
    return interactionSource
}

data class ToolTipperState(
    val tooltip: String = "Hello cupcake",
    val isVisible: Boolean = false
)

val LocalToolTipper = compositionLocalOf<ToolTipperModel> {
    error("No MyObject provided")
}