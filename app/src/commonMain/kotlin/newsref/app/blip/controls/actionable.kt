package newsref.app.blip.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import newsref.app.blip.nav.LocalNav
import newsref.app.blip.nav.LocalPortal
import newsref.app.blip.nav.NavRoute
import newsref.app.utils.modifyIfNotNull

@Composable
fun Modifier.actionable(route: NavRoute, isEnabled: Boolean = true): Modifier {
    val nav = LocalNav.current
    return this.actionable(route.title, isEnabled) { nav.go(route) }
}

@Composable
fun Modifier.actionable(
    hoverText: String? = null,
    isEnabled: Boolean = true,
    action: () -> Unit,
): Modifier {
    return if (isEnabled) {
        this
            .modifyIfNotNull(hoverText) {
                val source = remember { MutableInteractionSource() }
                val isHovered = source.collectIsHoveredAsState().value
                val portal = LocalPortal.current
                LaunchedEffect(isHovered) {
                    portal.setHoverText(if (isHovered) it else "")
                }
                this.hoverable(source)
            }
            .clickable(onClick = action)
    } else {
        this
    }
}
