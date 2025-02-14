package newsref.app.blip.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.X
import newsref.app.blip.controls.*
import newsref.app.blip.core.BlipConfig
import newsref.app.blip.theme.*

@Composable
fun Portal(
    currentRoute: NavRoute,
    config: BlipConfig,
    exitAction: (() -> Unit)?,
    content: @Composable () -> Unit
) {
    val nav = LocalNav.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blip.colors.background)
    ) {
        Row(
            horizontalArrangement = Blip.ruler.rowTight,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Blip.ruler.innerPadding)
        ) {
            // Icon(TablerIcons.Menu2)
            IconButton(config.logo) { nav.go(config.home) }
            Text(config.name)
            Spacer(modifier = Modifier.weight(1f))
            for (item in config.portalItems) {
                when (item) {
                    is PortalAction -> IconButton(item.icon) { item.action(nav) }
                    is PortalRoute -> {
                        val tint = if (currentRoute == item.route) {
                            Blip.colors.primary
                        } else {
                            Blip.colors.content
                        }
                        IconButton(item.icon, tint) { nav.go(item.route) }
                    }
                }
            }
            if (exitAction != null) {
                Spacer(modifier = Modifier.width(0.dp))
                IconButton(TablerIcons.X) { exitAction() }
            }
        }

        content()
    }
}

sealed class PortalItem {}

data class PortalAction(
    val icon: ImageVector,
    val label: String,
    val action: (Nav) -> Unit
) : PortalItem()

data class PortalRoute(
    val icon: ImageVector,
    val label: String,
    val route: NavRoute,
) : PortalItem()