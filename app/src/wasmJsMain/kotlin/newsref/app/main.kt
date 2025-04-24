package newsref.app

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Window
import pondui.ui.core.ProvideAddressContext

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        var browserAddress by remember { mutableStateOf(window.getPath()) }
        var appAddress by remember { mutableStateOf("") }

        window.addEventListener("hashchange") {
            val address = window.getPath()
            if (address == browserAddress || address == appAddress) return@addEventListener
            browserAddress = address
        }

        ProvideAddressContext(browserAddress, appConfig) {
            App(
                changeRoute = { navRoute ->
                    navRoute.toPath()?.let {
                        appAddress = it
                        window.location.hash = "/$it"
                    }
                },
                exitApp = null,
            )
        }
    }
}

private fun Window.getPath() = this.location.hash.substringAfter("#/", "")