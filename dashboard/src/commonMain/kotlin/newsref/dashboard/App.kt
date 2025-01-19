package newsref.dashboard

import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

import newsref.dashboard.ui.theme.AppTheme
import newsref.dashboard.utils.ToolTipper
import newsref.db.initDb

@Composable
@Preview
fun App(
    initialRoute: ScreenRoute,
    changeRoute: (ScreenRoute) -> Unit,
    exitApp: () -> Unit,
) {
    initDb()
    val context = AppContext(exitApp)
    AppTheme(true) {
        ToolTipper {
            Navigator(initialRoute, context, changeRoute)
        }
    }
}

class AppContext(
    val exitApp: () -> Unit
)
