package streetlight.web

import io.kvision.core.CssSize
import io.kvision.core.UNIT
import kotlinx.browser.window

object Layout {
    val spacing = 20
    val defaultPad = CssSize(spacing, UNIT.px)
    val defaultGap = spacing
    val halfPad = CssSize(spacing / 2, UNIT.px)
    val halfGap = spacing / 2
}

const val XHR_ERROR: Short = 0
const val HTTP_OK: Short = 200
const val HTTP_NO_CONTENT: Short = 204
const val HTTP_BAD_REQUEST: Short = 400
const val HTTP_UNAUTHORIZED: Short = 401
const val HTTP_FORBIDDEN: Short = 403
const val HTTP_NOT_FOUND: Short = 404
const val HTTP_NOT_ALLOWED: Short = 405
const val HTTP_SERVER_ERROR: Short = 500
const val HTTP_NOT_IMPLEMENTED: Short = 501
const val HTTP_BAD_GATEWAY: Short = 502
const val HTTP_SERVICE_UNAVAILABLE: Short = 503


val apiOrigin = if (window.location.href.contains("localhost")) {
    "http://192.168.1.122:8080"
} else {
    "https://streetlight.ing"
}
// const val baseAddress = "https://streetlight.ing"

val description = "Howdy, thanks for stopping by! " +
        "You can send a request or sing with me, just look below for my song list. " +
        "100% of your support goes toward the development of the streetlight app and community. " +
        "Let me know if you have any questions or feedback, and thank you for listening!"



