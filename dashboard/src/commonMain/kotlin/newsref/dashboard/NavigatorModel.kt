package newsref.dashboard

import newsref.dashboard.ui.screens.StateModel

class NavigatorModel(
    initialRoute: ScreenRoute
) : StateModel<NavigatorState>(NavigatorState(initialRoute)) {

    fun go(route: ScreenRoute) {
        setState { state -> state.copy(destination = route) }
    }

    fun setRoute(route: ScreenRoute) {
        setState { state -> state.copy(route = route) }
    }
}

data class NavigatorState(
    val route: ScreenRoute,
    val destination: ScreenRoute? = null
)