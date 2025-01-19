package newsref.dashboard

import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import newsref.dashboard.ui.screens.SourceItemRoute
import newsref.dashboard.ui.screens.SourceTableRoute

import newsref.dashboard.ui.theme.AppTheme
import newsref.dashboard.utils.ToolTipper
import newsref.db.initDb

@Composable
@Preview
fun App(
    exitApp: () -> Unit
) {
    initDb()
    val context = AppContext(exitApp)
    AppTheme(true) {
        ToolTipper {
            AppNavigator(SourceItemRoute(56577), context)
        }
    }
}

class AppContext(
    val exitApp: () -> Unit
)
