package newsref.app

import androidx.compose.runtime.*
import newsref.app.blip.core.Blapp
import newsref.app.blip.nav.NavRoute
import newsref.app.blip.theme.ProvideTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import newsref.app.blip.nav.Navigator
import newsref.app.blip.theme.ProvideSkyColors
import newsref.app.io.ProvideUserContext

@Composable
@Preview
fun App(
    initialRoute: NavRoute,
    changeRoute: (NavRoute) -> Unit,
    exitApp: (() -> Unit)?,
) {
    ProvideTheme {
        ProvideSkyColors {
            ProvideUserContext {
                Blapp(
                    initialRoute = initialRoute,
                    changeRoute = changeRoute,
                    config = appConfig,
                    exitApp = exitApp
                )
            }
        }
    }
}