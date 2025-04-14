package newsref.app.pond.core

import androidx.compose.runtime.*
import newsref.app.pond.nav.*
import newsref.app.pond.theme.*

@Composable
fun Blapp(
    initialRoute: NavRoute,
    changeRoute: (NavRoute) -> Unit,
    config: PondConfig,
    exitApp: (() -> Unit)?,
) {
    ProvideSkyColors {
        Navigator(
            startRoute = initialRoute,
            changeRoute = changeRoute,
            config = config,
            exitApp = exitApp
        )
    }
}