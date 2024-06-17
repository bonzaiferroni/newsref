package streetlight.app

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.transition.NavTransition

data class AppScene(
    val name: String,
    val route: String,
    val navTransition: NavTransition? = null,
    val content: @Composable (BackStackEntry, Navigator) -> Unit
) {
    fun go(navigator: Navigator?) = navigator?.navigate(route)
    fun go(navigator: Navigator?, id: Int) = navigator?.navigate("$route/$id")
}

fun RouteBuilder.appScenes(navigator: Navigator) {
    appScenes.forEach { nav ->
        scene(route = nav.route, navTransition = nav.navTransition) { bse ->
            nav.content(bse, navigator)
        }
    }
}