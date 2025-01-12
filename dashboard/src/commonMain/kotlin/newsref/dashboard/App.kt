package newsref.dashboard

import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

import newsref.dashboard.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme(true) {
        AppNavigator()
    }
}
