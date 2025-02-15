package newsref.app.blip.nav

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.X
import kotlinx.coroutines.NonCancellable.start
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
    val navState by nav.state.collectAsState()

    val infiniteTransition = rememberInfiniteTransition()
    val width = 2000f
    val offsetX by infiniteTransition.animateFloat(
        initialValue = width,
        targetValue = 0f, // Adjust for smooth looping
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (width * 20).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blip.colors.surface)
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColorList,
                    start = Offset(offsetX, 0f),
                    end = Offset(offsetX + width, 0f),
                    tileMode = TileMode.Repeated
                )
            )
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColorList,
                    start = Offset(-offsetX, 0f),
                    end = Offset(-offsetX - width, 0f),
                    tileMode = TileMode.Repeated
                )
            )
    ) {
        Row(
            horizontalArrangement = Blip.ruler.rowTight,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Blip.ruler.innerPadding)
        ) {
            val titleSpirit = remember { MutableInteractionSource() }
            // Icon(TablerIcons.Menu2)
            IconToggle(
                value = currentRoute == config.home,
                imageVector = config.logo,
                onHover = { nav.setHover(config.home, it) },
                interactionSource = titleSpirit
            ) { nav.go(config.home) }

            Text(
                text = config.name,
                style = Blip.typography.title,
                modifier = Modifier.hoverable(titleSpirit)
                    .clickable(interactionSource = null, indication = null, onClick = { nav.go(config.home)} )
            )
            val shownRoute = navState.hover ?: currentRoute
            val routeColor = when {
                shownRoute != currentRoute -> Blip.colors.accent.copy(.8f)
                else -> Blip.colors.content.copy(.6f)
            }
            Text(": ${shownRoute.title}", style = Blip.typography.title, color = routeColor)

            Spacer(modifier = Modifier.weight(1f))

            for (item in config.portalItems) {
                when (item) {
                    is PortalAction -> IconButton(item.icon) { item.action(nav) }
                    is PortalRoute -> {
                        IconToggle(
                            value = currentRoute == item.route,
                            imageVector = item.icon,
                            onHover = { nav.setHover(item.route, it) }
                        ) { nav.go(item.route) }
                    }
                }
            }
            if (exitAction != null) {
                Spacer(modifier = Modifier.width(0.dp))
                IconButton(TablerIcons.X) { exitAction() }
            }
        }

        Box(
            modifier = Modifier.padding(
                top = 0.dp,
                start = Blip.ruler.innerSpacing,
                end = Blip.ruler.innerSpacing,
                bottom = Blip.ruler.innerSpacing,
            )
        ) {
            content()
        }
    }
}

sealed class PortalItem

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

val gradientColorList = listOf(
    Color.Transparent,
    Color.Transparent,
    Color.White.copy(alpha = .1f),
    Color.White.copy(alpha = .1f),
    Color.Transparent,
    Color.Transparent,
    Color.White.copy(alpha = .2f),
    Color.Transparent,
)