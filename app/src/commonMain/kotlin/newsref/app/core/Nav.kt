package newsref.app.core

interface Nav {
    fun go(route: AppRoute)
    fun setRoute(route: AppRoute)
}