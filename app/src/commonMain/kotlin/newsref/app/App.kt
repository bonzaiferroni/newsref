package newsref.app

import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import io.pondlib.compose.ui.core.Blapp
import io.pondlib.compose.ui.nav.NavRoute
import io.pondlib.compose.ui.theme.ProvideSkyColors
import io.pondlib.compose.ui.theme.ProvideTheme
import io.pondlib.compose.ui.theme.defaultTheme
import io.pondlib.compose.ui.theme.useFamily
import newsref.app.generated.resources.Inter_18pt_Light
import newsref.app.generated.resources.Inter_18pt_Regular
import newsref.app.generated.resources.Inter_24pt_Light
import newsref.app.generated.resources.Inter_28pt_Light
import newsref.app.generated.resources.Res
import org.jetbrains.compose.ui.tooling.preview.Preview
import newsref.app.io.ProvideUserContext

@Composable
@Preview
fun App(
    initialRoute: NavRoute,
    changeRoute: (NavRoute) -> Unit,
    exitApp: (() -> Unit)?,
) {
    ProvideTheme(
        theme = defaultTheme(
            baseFont = useFamily(Res.font.Inter_18pt_Regular),
            h1Font = useFamily(Res.font.Inter_28pt_Light, FontWeight.Light),
            h2Font = useFamily(Res.font.Inter_24pt_Light, FontWeight.Light),
            h4Font = useFamily(Res.font.Inter_18pt_Light, FontWeight.Light),
        )
    ) {
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