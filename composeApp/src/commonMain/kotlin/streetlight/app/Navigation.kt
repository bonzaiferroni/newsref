package streetlight.app

import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.path
import streetlight.app.ui.debug.DebugScreen
import streetlight.app.ui.debug.AreaEditorScreen
import streetlight.app.ui.debug.AreaListScreen
import streetlight.app.ui.debug.EventEditorScreen
import streetlight.app.ui.debug.EventListScreen
import streetlight.app.ui.debug.LocationEditorScreen
import streetlight.app.ui.debug.LocationListScreen
import streetlight.app.ui.debug.SongEditorScreen
import streetlight.app.ui.debug.SongListScreen
import streetlight.app.ui.debug.RequestEditorScreen
import streetlight.app.ui.debug.RequestListScreen
import streetlight.app.ui.debug.UserEditorScreen
import streetlight.app.ui.main.EventProfileScreen
import streetlight.app.ui.main.NowScreen

object Scenes {
    val default = { now.route }

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

    val eventProfile = AppScene(
        name = "Event Profile",
        route = "/event/{id}/profile"
    ) { bse, navigator ->
        EventProfileScreen(bse.getId()!!, navigator)
    }

    val songList = AppScene(
        name = "Songs",
        route = "/songs"
    ) { _, navigator ->
        SongListScreen(navigator)
    }

    val songEditor = AppScene(
        name = "Create Song",
        route = "/song/{id}?"
    ) { bse, navigator ->
        SongEditorScreen(bse.getId(), navigator)
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

    val now = AppScene(
        name = "Now",
        route = "/now"
    ) { _, navigator ->
        NowScreen(navigator)
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
    Scenes.songList,
    Scenes.songEditor,
    Scenes.requestList,
    Scenes.requestEditor,
    Scenes.now,
    Scenes.eventProfile
)

