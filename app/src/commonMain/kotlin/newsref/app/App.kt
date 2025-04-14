package newsref.app

import androidx.compose.runtime.*
import newsref.app.pond.core.Blapp
import newsref.app.pond.nav.NavRoute
import newsref.app.pond.theme.ProvideTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import newsref.app.pond.theme.ProvideSkyColors
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