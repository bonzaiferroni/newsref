package newsref.dashboard

import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

import newsref.dashboard.ui.theme.AppTheme

@Composable
@Preview
fun App(
    exitApp: () -> Unit
) {
    val context = AppContext(exitApp)
    AppTheme(true) {
        AppNavigator(context)
    }
}

class AppContext(
    val exitApp: () -> Unit
)
