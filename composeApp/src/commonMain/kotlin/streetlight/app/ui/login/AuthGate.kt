package streetlight.app.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun AuthGate(
    navigator: Navigator?,
    content: @Composable () -> Unit
) {
    var isLoggedIn by remember { mutableStateOf(false) }

    if (isLoggedIn) {
        content()
    } else {
        navigator?.navigate("/login")
    }
}