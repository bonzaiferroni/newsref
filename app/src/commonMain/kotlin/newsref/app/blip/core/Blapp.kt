package newsref.app.blip.core

import androidx.compose.runtime.*
import newsref.app.blip.nav.NavRoute
import newsref.app.blip.nav.Navigator
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideColors
import newsref.app.blip.theme.ProvideTheme

@Composable
fun Blapp(
    initialRoute: NavRoute,
    changeRoute: (NavRoute) -> Unit,
    config: BlipConfig,
    exitApp: (() -> Unit)?,
) {
    ProvideTheme{
        ProvideColors(Blip.theme.skyColors) {
            Navigator(
                startRoute = initialRoute,
                changeRoute = changeRoute,
                config = config,
                exitApp = exitApp
            )
        }
    }
}