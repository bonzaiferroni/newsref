package newsref.dashboard.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.lifecycle.viewmodel.compose.viewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Click
import compose.icons.tablericons.InfoCircle
import newsref.dashboard.halfPadding
import newsref.dashboard.halfSpacing
import newsref.dashboard.ui.screens.StateModel

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
                horizontalArrangement = Arrangement.spacedBy(halfSpacing),
                modifier = Modifier.padding(halfPadding),
            ) {
                val icon = when (state.tooltip.type) {
                    TipType.Information -> TablerIcons.InfoCircle
                    TipType.Action -> TablerIcons.Click
                }
                Icon(icon, "Tip Icon")
                Text(state.tooltip.text)
            }
        }
    }
}

class ToolTipperModel : StateModel<ToolTipperState>(ToolTipperState()) {
    fun setTip(tip: ToolTip) {
        setState { it.copy(tooltip = tip, isVisible = true) }
    }

    fun releaseTip(tip: ToolTip) {
        if (stateNow.tooltip != tip) return
        setState { it.copy(tooltip = tip, isVisible = false) }
    }
}

data class ToolTipperState(
    val tooltip: ToolTip = ToolTip("Hello Cupcake"),
    val isVisible: Boolean = false,
)

data class ToolTip(
    val text: String,
    val type: TipType = TipType.Information,
)

enum class TipType {
    Information,
    Action
}

@Composable
fun SetToolTip(tip: ToolTip, interactionSource: InteractionSource) {
    val isHovered by interactionSource.collectIsHoveredAsState()
    val toolTipper = LocalToolTipper.current
    if (isHovered) {
        toolTipper.setTip(tip)
    } else {
        toolTipper.releaseTip(tip)
    }
}

val LocalToolTipper = compositionLocalOf<ToolTipperModel> {
    error("No MyObject provided")
}