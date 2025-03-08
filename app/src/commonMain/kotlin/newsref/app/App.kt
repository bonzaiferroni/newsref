package newsref.app

import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import newsref.app.blip.core.Blapp
import newsref.app.blip.nav.NavRoute
import newsref.app.blip.theme.ProvideTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import newsref.app.blip.nav.Navigator

@Composable
@Preview
fun App(
    initialRoute: NavRoute,
    dataStore: DataStore<Preferences>,
    changeRoute: (NavRoute) -> Unit,
    exitApp: (() -> Unit)?,
) {
    ProvideKeyStore(dataStore) {
        Blapp(
            initialRoute = initialRoute,
            changeRoute = changeRoute,
            config = appConfig,
            exitApp = exitApp
        )
    }
}