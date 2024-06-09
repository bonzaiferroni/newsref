package streetlight.app

import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.transition.NavTransition
import streetlight.app.ui.HomeScreen
import streetlight.app.ui.data.AreaCreatorScreen
import streetlight.app.ui.data.AreaListScreen
import streetlight.app.ui.data.EventCreatorScreen
import streetlight.app.ui.data.EventListScreen
import streetlight.app.ui.data.LocationEditScreen
import streetlight.app.ui.data.LocationListScreen
import streetlight.app.ui.data.UserEditScreen

object AppRoutes {
    const val default = "/home"
}

fun RouteBuilder.appScenes(navigator: Navigator) {
    // Define a scene to the navigation graph
    scene(
        // Scene's route path
        route = "/home",
        // Navigation transition for this scene, this is optional
        navTransition = NavTransition(),
    ) {
        HomeScreen(navigator)
    }
    scene("/user/{id}?") { UserEditScreen(it.getId(), navigator) }
    scene("/locations") { LocationListScreen(navigator) }
    scene("/location/{id}?") { LocationEditScreen(it.getId(), navigator) }
    scene("/areas") { AreaListScreen(navigator) }
    scene("/area/{id}?") { AreaCreatorScreen(it.getId(), navigator) }
    scene("/events") { EventListScreen(navigator) }
    scene("/event/{id}?") { EventCreatorScreen(it.getId(), navigator) }
}

fun BackStackEntry.getId() = this.path<Int>("id")