package newsref.app.blip.nav

interface Nav {
    fun go(route: NavRoute)
    fun setRoute(route: NavRoute)
}