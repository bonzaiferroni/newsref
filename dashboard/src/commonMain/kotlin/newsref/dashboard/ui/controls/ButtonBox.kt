package newsref.dashboard.ui.controls

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import newsref.dashboard.utils.SetToolTip
import newsref.dashboard.utils.ToolTip
import newsref.app.utils.modifyIfNotNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonBox(
    toolTip: ToolTip? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    toolTip?.let { SetToolTip(it, interactionSource) }

    val hoverAlpha = remember { Animatable(0f) }
    val isHovered = interactionSource.collectIsHoveredAsState()
    val isPressed = interactionSource.collectIsPressedAsState()

    if (onClick != null || toolTip != null) {
        LaunchedEffect(isHovered.value) {
            val target = if (isHovered.value) {
                .15f
            } else {
                0f
            }
            hoverAlpha.animateTo(target, tween(200, 0, EaseOut))
        }
    }
    if (onClick != null) {
        LaunchedEffect(isPressed.value) {
            val target = if (isPressed.value) {
                .3f
            } else if (isHovered.value) {
                .15f
            } else {
                0f
            }
            hoverAlpha.animateTo(target, tween(100, 0, EaseOut))
        }
    }

    Box(
        modifier = modifier
            .modifyIfNotNull(onClick) {
                this.clickable(onClick = it, indication = null, interactionSource = interactionSource)
            }
            .modifyIfNotNull(interactionSource) { this.hoverable(it) }
            .drawBehind {
                val padding = 3.dp.toPx()
                drawRoundRect(
                    color = Color.White.copy(alpha = hoverAlpha.value),
                    size = size.copy(width = size.width + padding * 2, height = size.height + padding * 2),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    topLeft = Offset(-padding, -padding)
                )
            }
    ) {
        content()
    }
}
