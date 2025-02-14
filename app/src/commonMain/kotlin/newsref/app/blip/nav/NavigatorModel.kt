package newsref.app.blip.nav

import androidx.navigation.NavController
import newsref.app.blip.core.StateModel

class NavigatorModel(
    initialRoute: NavRoute,
    private val navController: NavController,
    private val onChangeRoute: (NavRoute) -> Unit,
) : StateModel<NavigatorState>(NavigatorState(initialRoute)), Nav {

    override fun go(route: NavRoute) {
        navController.navigate(route)
        onChangeRoute(route)
        setState { state -> state.copy(route = route, destination = route) }
    }

    override fun setRoute(route: NavRoute) {
        onChangeRoute(route)
        setState { state -> state.copy(route = route) }
    }
}

data class NavigatorState(
    val route: NavRoute,
    val destination: NavRoute? = null
)