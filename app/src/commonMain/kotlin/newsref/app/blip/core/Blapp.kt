package newsref.app.blip.core

import androidx.compose.runtime.*
import newsref.app.blip.nav.*
import newsref.app.blip.theme.*

@Composable
fun Blapp(
    initialRoute: NavRoute,
    changeRoute: (NavRoute) -> Unit,
    config: BlipConfig,
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