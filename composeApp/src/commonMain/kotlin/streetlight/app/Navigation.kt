package streetlight.app

import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.path
import streetlight.app.ui.DebugScreen
import streetlight.app.ui.data.AreaEditorScreen
import streetlight.app.ui.data.AreaListScreen
import streetlight.app.ui.data.EventEditorScreen
import streetlight.app.ui.data.EventListScreen
import streetlight.app.ui.data.LocationEditorScreen
import streetlight.app.ui.data.LocationListScreen
import streetlight.app.ui.data.PerformanceEditorScreen
import streetlight.app.ui.data.PerformanceListScreen
import streetlight.app.ui.data.RequestEditorScreen
import streetlight.app.ui.data.RequestListScreen
import streetlight.app.ui.data.UserEditorScreen

object Scenes {
    val default = { debug.route }

    val debug = AppScene(
        name = "Debug",
        route = "/debug",
    ) { _, navigator ->
        DebugScreen(navigator)
    }

    val userEditor = AppScene(
        name = "Create User",
        route = "/user/{id}?"
    ) { bse, navigator ->
        UserEditorScreen(bse.getId(), navigator)
    }

    val locationList = AppScene(
        name = "Locations",
        route = "/locations"
    ) { _, navigator ->
        LocationListScreen(navigator)
    }

    val locationEditor = AppScene(
        name = "Create Location",
        route = "/location/{id}?"
    ) { bse, navigator ->
        LocationEditorScreen(bse.getId(), navigator)
    }

    val areaList = AppScene(
        name = "Areas",
        route = "/areas"
    ) { _, navigator ->
        AreaListScreen(navigator)
    }

    val areaEditor = AppScene(
        name = "Create Area",
        route = "/area/{id}?"
    ) { bse, navigator ->
        AreaEditorScreen(bse.getId(), navigator)
    }

    val eventList = AppScene(
        name = "Events",
        route = "/events"
    ) { _, navigator ->
        EventListScreen(navigator)
    }

    val eventEditor = AppScene(
        name = "Create Event",
        route = "/event/{id}?"
    ) { bse, navigator ->
        EventEditorScreen(bse.getId(), navigator)
    }

    val performanceList = AppScene(
        name = "Performances",
        route = "/performances"
    ) { _, navigator ->
        PerformanceListScreen(navigator)
    }

    val performanceEditor = AppScene(
        name = "Create Performance",
        route = "/performance/{id}?"
    ) { bse, navigator ->
        PerformanceEditorScreen(bse.getId(), navigator)
    }

    val requestList = AppScene(
        name = "Requests",
        route = "/requests"
    ) { _, navigator ->
        RequestListScreen(navigator)
    }

    val requestEditor = AppScene(
        name = "Create Request",
        route = "/request/{id}?"
    ) { bse, navigator ->
        RequestEditorScreen(bse.getId(), navigator)
    }
}

fun BackStackEntry.getId() = this.path<Int>("id")

val appScenes = listOf(
    Scenes.debug,
    Scenes.userEditor,
    Scenes.locationList,
    Scenes.locationEditor,
    Scenes.areaList,
    Scenes.areaEditor,
    Scenes.eventList,
    Scenes.eventEditor,
    Scenes.performanceList,
    Scenes.performanceEditor,
    Scenes.requestList,
    Scenes.requestEditor
)

