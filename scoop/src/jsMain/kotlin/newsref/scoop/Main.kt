package newsref.scoop

import kotlinx.browser.window
import kotlinx.coroutines.delay

suspend fun main() {
    console.log("ey!!!")
    var body = window.document.body
    while (body == null) {
        delay(10)
        body = window.document.body
    }

    body.style.border = "5px solid red"
    console.log(body)
}