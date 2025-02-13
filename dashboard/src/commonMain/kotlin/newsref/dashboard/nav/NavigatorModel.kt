package newsref.dashboard.nav

import newsref.dashboard.ScreenRoute
import newsref.dashboard.ui.screens.StateModel

class NavigatorModel(
    initialRoute: ScreenRoute
) : StateModel<NavigatorState>(NavigatorState(initialRoute)) {

    fun go(route: ScreenRoute) {
        setState { state -> state.copy(route = route, destination = route) }
    }

    fun setRoute(route: ScreenRoute) {
        setState { state -> state.copy(route = route) }
    }
}

data class NavigatorState(
    val route: ScreenRoute,
    val destination: ScreenRoute? = null
)