package newsref.app.nav

import newsref.app.core.AppRoute
import newsref.app.core.Nav
import newsref.app.core.StateModel

class AppNavigatorModel(
    initialRoute: AppRoute
) : StateModel<NavigatorState>(NavigatorState(initialRoute)), Nav {

    override fun go(route: AppRoute) {
        setState { state -> state.copy(route = route, destination = route) }
    }

    override fun setRoute(route: AppRoute) {
        setState { state -> state.copy(route = route) }
    }
}

data class NavigatorState(
    val route: AppRoute,
    val destination: AppRoute? = null
)