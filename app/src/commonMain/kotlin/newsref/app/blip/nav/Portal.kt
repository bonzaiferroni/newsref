package newsref.app.blip.nav

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import newsref.app.utils.modifyIfNotNull

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun Portal(
    currentRoute: NavRoute,
    config: BlipConfig,
    exitAction: (() -> Unit)?,
    viewModel: PortalModel = viewModel { PortalModel() },
    content: @Composable () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val nav = LocalNav.current
    val navState by nav.state.collectAsState()
    val hazeState = remember { HazeState() }

    CompositionLocalProvider(LocalPortal provides viewModel) {
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

                PortalTitle(state.hoverText, currentRoute)

                if (exitAction != null) {
                    Spacer(modifier = Modifier.width(0.dp))
                    IconButton(TablerIcons.X) { exitAction() }
                }
            }

            SlideIn(
                show = state.bottomBarIsVisible,
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Row(
                    horizontalArrangement = Blip.ruler.rowGrouped,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                        .height(portalBottomBarHeight)
                        .shadow(Blip.ruler.shadowElevation, RoundedCornerShape(
                            topStartPercent = 60, topEndPercent = 60,
                            bottomStartPercent = 0, bottomEndPercent = 0
                        ))
                        .pointerInput(Unit) { }
                        .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin(hazeBackground))
                        .padding(Blip.ruler.halfPadding)
                ) {
                    for (item in config.portalItems) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxHeight()
                                .aspectRatio(1f)
                                .modifyIfNotNull(item as? PortalAction) { this.actionable { it.action(nav) } }
                                .modifyIfNotNull(item as? PortalRoute) {
                                    if (it != currentRoute) {
                                        this.actionable(it.route)
                                    } else {
                                        this
                                    }
                                }
                        ) {
                            Icon(
                                imageVector = item.icon, tint = Blip.localColors.contentDim,
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            )
                            Label(item.label)
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun RowScope.PortalTitle(
    hoverText: String,
    currentRoute: NavRoute,
) {
    Box(
        modifier = Modifier.weight(1f)
    ) {
        val hoverVisible = hoverText.isNotEmpty()
        AnimatedContent(targetState = hoverVisible, label = "Visibility") { visible ->
            if (hoverVisible) {
                Text(
                    text = hoverText,
                    style = Blip.typo.title.copy(textAlign = TextAlign.Center),
                    color = Blip.colors.shine.copy(.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = currentRoute.title,
                    style = Blip.typo.title.copy(textAlign = TextAlign.Center),
                    color = Blip.localColors.content.copy(.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

sealed class PortalItem {
    abstract val icon: ImageVector
    abstract val label: String
}

data class PortalAction(
    override val icon: ImageVector,
    override val label: String,
    val action: (Nav) -> Unit
) : PortalItem()

data class PortalRoute(
    override val icon: ImageVector,
    override val label: String,
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

val LocalPortal = staticCompositionLocalOf<PortalModel> {
    error("No portal provided")
}

val portalTopBarHeight = 50.dp
val portalBottomBarHeight = 70.dp

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