package streetlight.app

import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition
import streetlight.app.ui.HomeScreen
import streetlight.app.ui.data.AreaCreatorScreen
import streetlight.app.ui.data.AreaListScreen
import streetlight.app.ui.data.EventCreatorScreen
import streetlight.app.ui.data.EventListScreen
import streetlight.app.ui.data.LocationCreatorScreen
import streetlight.app.ui.data.LocationListScreen
import streetlight.app.ui.data.UserCreatorScreen

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
    scene("/createUser") { UserCreatorScreen(navigator) }
    scene("/location") { LocationListScreen(navigator) }
    scene("/createLocation") { LocationCreatorScreen(navigator) }
    scene("/area") { AreaListScreen(navigator) }
    scene("/createArea") { AreaCreatorScreen(navigator) }
    scene("/event") { EventListScreen(navigator) }
    scene("/createEvent") { EventCreatorScreen(navigator) }

}