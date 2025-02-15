package newsref.dashboard.ui.table

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.Copy
import compose.icons.tablericons.ExternalLink
import newsref.dashboard.ui.theme.primaryDark
import newsref.dashboard.utils.SetToolTip
import newsref.dashboard.utils.TipType
import newsref.dashboard.utils.ToolTip
import newsref.app.utils.modifyIfNotNull
import newsref.dashboard.utils.setRawText

@Composable
fun <T> CellControlRow(
    item: T,
    controls: List<CellControl<T>>,
) {
    if (controls.isEmpty()) return
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    Row() {
        for (control in controls) {
            val controlInteractionSource = remember { MutableInteractionSource() }
            control.toolTip?.let { SetToolTip(it, controlInteractionSource) }

            IconButton(
                onClick = {
                    when (control) {
                        is ActionControl<T> -> control.onClick(item!!)
                        is ClipboardControl<T> -> control.onClick(clipboardManager, item!!)
                        is UrlControl<T> -> control.onClick(uriHandler, item!!)
                    }
                },
                modifier = Modifier.size(24.dp)
                    .focusProperties { canFocus = false }
                    .modifyIfNotNull(controlInteractionSource) { this.hoverable(it) },
                colors = IconButtonDefaults.iconButtonColors(contentColor = primaryDark),
            ) {
                Icon(imageVector = control.icon, contentDescription = "cell control")
            }
        }
    }
}

data class ActionControl<T>(
    override val icon: ImageVector,
    override val toolTip: ToolTip? = null,
    val onClick: (T) -> Unit,
) : CellControl<T>()

data class ClipboardControl<T>(
    override val icon: ImageVector,
    override val toolTip: ToolTip? = null,
    val onClick: ClipboardManager.(T) -> Unit
) : CellControl<T>()

data class UrlControl<T>(
    override val icon: ImageVector,
    override val toolTip: ToolTip? = null,
    val onClick: UriHandler.(T) -> Unit
) : CellControl<T>()

sealed class CellControl<T> {
    abstract val icon: ImageVector
    abstract val toolTip: ToolTip?
}

fun <T> openExternalLink(toolTip: String = "Open external link", block: (T) -> String) =
    UrlControl<T>(TablerIcons.ExternalLink, ToolTip(toolTip, TipType.Action)) { this.openUri(block(it)) }

fun <T> copyText(toolTip: String = "Copy to clipboard", block: (T) -> String?) =
    ClipboardControl<T>(TablerIcons.Copy, ToolTip(toolTip, TipType.Action)) { block(it)?.let { this.setRawText(it) } }