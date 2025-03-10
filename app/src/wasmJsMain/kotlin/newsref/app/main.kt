package newsref.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        App(StartRoute, {
            // val route = AppPathAdapter.toString(it)
            // println(route)
            // window.history.pushState(null, "", route)
        }, null)
    }
}