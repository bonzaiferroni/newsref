package newsref.app

import androidx.compose.runtime.*
import newsref.app.blip.core.Blapp
import newsref.app.blip.nav.NavRoute
import newsref.app.blip.theme.ProvideTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import newsref.app.blip.nav.Navigator

@Composable
@Preview
fun App(
    initialRoute: NavRoute,
    changeRoute: (NavRoute) -> Unit,
    exitApp: (() -> Unit)?,
) {
    Blapp(
        initialRoute = initialRoute,
        changeRoute = changeRoute,
        config = appConfig,
        exitApp = exitApp
    )
}