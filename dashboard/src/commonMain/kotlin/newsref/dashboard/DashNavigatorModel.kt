package newsref.dashboard

import newsref.app.pond.core.StateModel

class DashNavigatorModel(
    initialRoute: DashRoute
) : StateModel<NavigatorState>(NavigatorState(initialRoute)) {

    fun go(route: DashRoute) {
        setState { state -> state.copy(route = route, destination = route) }
    }

    fun setRoute(route: DashRoute) {
        setState { state -> state.copy(route = route) }
    }
}

data class NavigatorState(
    val route: DashRoute,
    val destination: DashRoute? = null
)