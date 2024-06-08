package streetlight.app

import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition
import streetlight.app.ui.HomeScreen
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
    scene("/createUser") {
        UserCreatorScreen()
    }
    scene("/createLocation") {
        LocationCreatorScreen()
    }
    scene("/location") {
        LocationListScreen(navigator)
    }
}