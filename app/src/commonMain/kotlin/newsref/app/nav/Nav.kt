package newsref.app.nav

interface Nav {
    fun go(route: NavRoute)
    fun setRoute(route: NavRoute)
}