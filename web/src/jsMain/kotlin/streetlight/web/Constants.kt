package streetlight.web

import io.kvision.core.CssSize
import io.kvision.core.UNIT
import io.kvision.utils.px

object Constants {
    val spacing = 20
    val defaultPad = CssSize(spacing, UNIT.px)
    val defaultGap = spacing / 2
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