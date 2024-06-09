package streetlight.app

import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.transition.NavTransition
import streetlight.app.ui.HomeScreen
import streetlight.app.ui.data.AreaEditorScreen
import streetlight.app.ui.data.AreaListScreen
import streetlight.app.ui.data.EventEditorScreen
import streetlight.app.ui.data.EventListScreen
import streetlight.app.ui.data.LocationEditorScreen
import streetlight.app.ui.data.LocationListScreen
import streetlight.app.ui.data.UserEditorScreen

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
    scene("/user/{id}?") { UserEditorScreen(it.getId(), navigator) }
    scene("/locations") { LocationListScreen(navigator) }
    scene("/location/{id}?") { LocationEditorScreen(it.getId(), navigator) }
    scene("/areas") { AreaListScreen(navigator) }
    scene("/area/{id}?") { AreaEditorScreen(it.getId(), navigator) }
    scene("/events") { EventListScreen(navigator) }
    scene("/event/{id}?") { EventEditorScreen(it.getId(), navigator) }
}

fun BackStackEntry.getId() = this.path<Int>("id")