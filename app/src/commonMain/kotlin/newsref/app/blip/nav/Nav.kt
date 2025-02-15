package newsref.app.blip.nav

import kotlinx.coroutines.flow.StateFlow

interface Nav {
    val state: StateFlow<NavState>

    fun go(route: NavRoute)
    fun setRoute(route: NavRoute)
    fun setHover(route: NavRoute, isHovered: Boolean)
}