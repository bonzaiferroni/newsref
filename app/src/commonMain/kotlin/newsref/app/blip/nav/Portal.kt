package newsref.app.blip.nav

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowBack
import compose.icons.tablericons.X
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import newsref.app.blip.behavior.SlideIn
import newsref.app.blip.controls.*
import newsref.app.blip.core.BlipConfig
import newsref.app.blip.theme.*
import newsref.app.utils.darken

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun Portal(
    currentRoute: NavRoute,
    config: BlipConfig,
    exitAction: (() -> Unit)?,
    content: @Composable () -> Unit
) {
    val nav = LocalNav.current
    val navState by nav.state.collectAsState()
    val hazeState = remember { HazeState() }
    val topBarHeight = 50.dp
    val bottomBarHeight = 70.dp
    var showBottomBar by remember { mutableStateOf(true) }
    val portalConfig = PortalConfig(
        topSpacing = topBarHeight,
        bottomSpacing = bottomBarHeight,
        showBottomNav = {
            showBottomBar = it
        }
    )
    CompositionLocalProvider(LocalPortalConfig provides portalConfig) {
        Box(
            modifier = Modifier
                .background(Blip.colors.background)
                .fillMaxSize()
        ) {
            val barHeight = 50.dp
            val hazeBackground = Blip.colors.background.darken(-.1f)
            Box(
                modifier = Modifier
                    .hazeSource(state = hazeState)
                    .padding(
                        top = 0.dp,
                        start = Blip.ruler.innerSpacing,
                        end = Blip.ruler.innerSpacing,
                        bottom = 0.dp,
                    )
            ) {
                content()
            }

            Row(
                horizontalArrangement = Blip.ruler.rowTight,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .shadow(Blip.ruler.shadowElevation)
                    .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin(hazeBackground))
                    .height(barHeight)
                    .padding(Blip.ruler.innerPadding)
            ) {
                IconToggle(
                    value = currentRoute == config.home,
                    imageVector = TablerIcons.ArrowBack,
                    enabled = navState.canGoBack,
                ) { nav.goBack() }

                PortalTitle(currentRoute, config)

                if (exitAction != null) {
                    Spacer(modifier = Modifier.width(0.dp))
                    IconButton(TablerIcons.X) { exitAction() }
                }
            }

            SlideIn(
                show = showBottomBar,
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Row(
                    horizontalArrangement = Blip.ruler.rowTight,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                        .height(barHeight)
                        .shadow(Blip.ruler.shadowElevation, Blip.ruler.roundedTop)
                        .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin(hazeBackground))
                        .padding(Blip.ruler.innerPadding)
                ) {
                    for (item in config.portalItems) {
                        when (item) {
                            is PortalAction -> IconButton(item.icon) { item.action(nav) }
                            is PortalRoute -> {
                                IconToggle(
                                    value = currentRoute == item.route,
                                    imageVector = item.icon,
                                    modifier = Modifier.isHovered { nav.setHover(item.route, it) }
                                ) { nav.go(item.route) }
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun RowScope.PortalTitle(
    currentRoute: NavRoute,
    config: BlipConfig
) {
    val nav = LocalNav.current
    val navState by nav.state.collectAsState()

    var hoverTitleDisplay by remember { mutableStateOf("") }

    LaunchedEffect(navState.hover) {
        navState.hover?.let { hoverTitleDisplay = it.title }
    }

    Box(
        modifier = Modifier.weight(1f)
    ) {
        val hoverVisible = navState.hover != null && navState.hover != currentRoute
        AnimatedContent(targetState = hoverVisible, label = "Visibility") { visible ->
            if (visible) {
                Text(
                    text = hoverTitleDisplay,
                    style = Blip.typ.title.copy(textAlign = TextAlign.Center),
                    color = Blip.colors.shine.copy(.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = currentRoute.title,
                    style = Blip.typ.title.copy(textAlign = TextAlign.Center),
                    color = Blip.localColors.content.copy(.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
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

data class PortalConfig(
    val topSpacing: Dp,
    val bottomSpacing: Dp,
    val showBottomNav: (Boolean) -> Unit
)

val LocalPortalConfig = staticCompositionLocalOf<PortalConfig> {
    error("No Nav provided")
}

//            .drawBehind {
//                drawRect(
//                    brush = Brush.linearGradient(
//                        colors = gradientColorList,
//                        start = Offset(offsetX, 0f),
//                        end = Offset(offsetX + width, 0f),
//                        tileMode = TileMode.Repeated
//                    )
//                )
//                drawRect(
//                    brush = Brush.linearGradient(
//                        colors = gradientColorList,
//                        start = Offset(-offsetX, 0f),
//                        end = Offset(-offsetX - width, 0f),
//                        tileMode = TileMode.Repeated
//                    )
//                )
//            }
//

//val infiniteTransition = rememberInfiniteTransition()
//val width = 2000f
//val offsetX by infiniteTransition.animateFloat(
//    initialValue = width,
//    targetValue = 0f, // Adjust for smooth looping
//    animationSpec = infiniteRepeatable(
//        animation = tween(durationMillis = (width * 20).toInt(), easing = LinearEasing),
//        repeatMode = RepeatMode.Restart
//    )
//)