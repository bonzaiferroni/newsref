package newsref.dashboard

import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import newsref.dashboard.ui.screens.FeedTableRoute
import newsref.dashboard.ui.screens.SourceTableRoute

import newsref.dashboard.ui.theme.AppTheme
import newsref.db.initDb

@Composable
@Preview
fun App(
    exitApp: () -> Unit
) {
    initDb()
    val context = AppContext(exitApp)
    AppTheme(true) {
        AppNavigator(SourceTableRoute, context)
    }
}

class AppContext(
    val exitApp: () -> Unit
)
