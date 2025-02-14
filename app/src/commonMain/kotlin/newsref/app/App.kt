package newsref.app

import androidx.compose.runtime.*
import newsref.app.nav.NavRoute
import newsref.app.fui.ProvideTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import newsref.app.nav.Navigator

@Composable
@Preview
fun App(
    initialRoute: NavRoute,
    changeRoute: (NavRoute) -> Unit,
    exitApp: () -> Unit,
) {
    ProvideTheme{
        Navigator(initialRoute, changeRoute)
    }
}