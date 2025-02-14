package newsref.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    document.body!!.style.padding = "0"
    ComposeViewport(document.body!!) {
        App(StartRoute, { }, null)
    }
}