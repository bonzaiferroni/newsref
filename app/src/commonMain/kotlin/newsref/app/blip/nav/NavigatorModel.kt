package newsref.app.blip.nav

import androidx.navigation.NavController
import newsref.app.blip.core.StateModel

class NavigatorModel(
    initialRoute: NavRoute,
    private val navController: NavController,
) : StateModel<NavState>(NavState(initialRoute)), Nav {

    private val backStack: MutableList<NavRoute> = mutableListOf()

    override fun go(route: NavRoute) {
        backStack.add(stateNow.route)
        if (backStack.size > 40) backStack.removeAt(0)
        navController.navigate(route)
        setState { state ->
            state.copy(
                route = route,
                backRoute = backStack.lastOrNull()
            )
        }
    }

    override fun goBack() {
        if (backStack.isEmpty()) return
        val next = backStack.removeLast()
        navController.navigateUp()
        setRoute(next)
    }

    override fun setRoute(route: NavRoute) {
        setState { state ->
            state.copy(
                route = route,
                backRoute = backStack.lastOrNull()
            )
        }
    }
}

data class NavState(
    val route: NavRoute,
    val backRoute: NavRoute? = null,
)

const val NAV_KEY = "nav_route"