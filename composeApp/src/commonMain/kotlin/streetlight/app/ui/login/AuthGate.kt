package streetlight.app.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.Navigator

@Composable
fun AuthGate(
    navigator: Navigator?,
    content: @Composable () -> Unit
) {
    var isLoggedIn by remember { mutableStateOf(false) }

    if (isLoggedIn) {
        content()
    } else {
        navigator?.push(LoginScreen() {
            isLoggedIn = true
            navigator.pop()
        })
    }
}