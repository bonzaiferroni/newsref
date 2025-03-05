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
                hover = null,
                canGoBack = backStack.isNotEmpty()
            )
        }
    }

    override fun goBack() {
        if (!stateNow.canGoBack) return
        val next = backStack.removeLast()
        navController.navigateUp()
        setRoute(next)
    }

    override fun setRoute(route: NavRoute) {
        setState { state ->
            state.copy(
                route = route,
                canGoBack = backStack.isNotEmpty()
            )
        }
    }

    override fun setHover(route: NavRoute, isHovered: Boolean) {
        if (isHovered) {
            setState { it.copy(hover = route) }
        } else if (stateNow.hover == route) {
            setState { it.copy(hover = null) }
        }
    }
}

data class NavState(
    val route: NavRoute,
    val hover: NavRoute? = null,
    val canGoBack: Boolean = false
)