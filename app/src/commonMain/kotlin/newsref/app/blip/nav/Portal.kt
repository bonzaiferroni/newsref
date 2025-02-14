package newsref.app.blip.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import kotlinx.collections.immutable.ImmutableList
import newsref.app.blip.controls.*
import newsref.app.blip.theme.*

@Composable
fun Portal(
    logo: ImageVector,
    logoAction: () -> Unit = { },
    actions: ImmutableList<PortalAction>,
    content: @Composable () -> Unit
) {
    val nav = LocalNav.current
    Column(
        verticalArrangement = Blip.layout.columnSpaced,
        modifier = Modifier.background(Blip.colors.background)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Blip.layout.rowGrouped
            ) {
                Icon(TablerIcons.Menu2)
                IconButton(logo, action = logoAction)
            }
            Row(
                horizontalArrangement = Blip.layout.rowGrouped
            ) {
                for (action in actions) {
                    IconButton(action.icon) { action.onClick(nav) }
                }
            }
        }

        Box(
            modifier = Modifier.background(Blip.colors.surface)
        ) {
            content()
        }
    }
}

data class PortalAction(
    val icon: ImageVector,
    val label: String,
    val onClick: (Nav) -> Unit
)