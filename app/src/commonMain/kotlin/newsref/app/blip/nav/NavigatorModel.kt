package newsref.app.blip.nav

import androidx.navigation.NavController
import newsref.app.blip.core.StateModel

class NavigatorModel(
    initialRoute: NavRoute,
    private val navController: NavController,
) : StateModel<NavState>(NavState(initialRoute)), Nav {

    override fun go(route: NavRoute) {
        navController.navigate(route)
        setState { state -> state.copy(route = route, destination = route) }
    }

    override fun setRoute(route: NavRoute) {
        setState { state -> state.copy(route = route) }
    }

    override fun setHover(route: NavRoute, isHovered: Boolean) {
        if (isHovered) {
            setState { it.copy(hover = route) }
        } else if (stateNow.hover == route) {
            setState { it.copy(hover = null)}
        }
    }
}

data class NavState(
    val route: NavRoute,
    val destination: NavRoute? = null,
    val hover: NavRoute? = null,
)