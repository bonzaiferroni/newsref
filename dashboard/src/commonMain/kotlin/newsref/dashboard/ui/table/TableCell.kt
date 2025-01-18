package newsref.dashboard.ui.table

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import newsref.dashboard.innerPadding
import newsref.dashboard.ui.theme.primaryDark
import newsref.dashboard.utils.SetToolTip
import newsref.dashboard.utils.ToolTip
import newsref.dashboard.utils.modifyIfNotNull

@Composable
fun <T> RowScope.TableCell(
    width: Int? = null,
    item: T? = null,
    color: Color = Color(0f, 0f, 0f, 0f),
    alignCell: AlignCell? = null,
    weight: Float? = null,
    toolTip: ToolTip? = null,
    onClickCell: ((T) -> Unit)? = null,
    controls: List<CellControl<T>> = emptyList(),
    padding: PaddingValues = innerPadding,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val interactionSource = toolTip?.let { SetToolTip(it) }

    Row(
        modifier = modifier
            .modifyIfNotNull(width) { this.width(it.dp) }
            .modifyIfNotNull(weight) { this.weight(it) }
            .background(color = color)
            .padding(padding)
            .modifyIfNotNull(interactionSource) { this.hoverable(it) }
    ) {
        Box(
            modifier = Modifier.weight(1f)
                .modifyIfNotNull(onClickCell) { this.clickable(onClick = { it(item!!) }) }
        ) {
            val alignment = if (alignCell == AlignCell.Right) {
                Alignment.TopEnd
            } else {
                Alignment.TopStart
            }
            Box(modifier = Modifier.align(alignment)) {
                content()
            }
        }

        if (controls.isNotEmpty()) {
            Row() {
                for (control in controls) {
                    IconButton(
                        onClick = { control.onClick(item!!) },
                        modifier = Modifier.size(24.dp).focusProperties { canFocus = false },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = primaryDark),
                    ) {
                        Icon(imageVector = control.icon, contentDescription = "cell control")
                    }
                }
            }
        }
    }
}

enum class AlignCell {
    Left,
    Right
}