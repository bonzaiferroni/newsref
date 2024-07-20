package streetlight.app.ui.core

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.Navigator
import streetlight.app.Scenes
import streetlight.app.chopui.ChopScaffold
import streetlight.app.chopui.FabConfig

@Composable
fun AppScaffold(
    title: String,
    navigator: Navigator?,
    fabConfig: FabConfig? = null,
    content: @Composable () -> Unit,
) {
    ChopScaffold(
        title = title,
        navigator = navigator,
        routes = listOf(Scenes.now.route, Scenes.debug.route),
        fabConfig = fabConfig,
    ) {
        content()
    }
}