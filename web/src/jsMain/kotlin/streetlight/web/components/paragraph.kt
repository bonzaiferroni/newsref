package streetlight.web.components

import io.kvision.core.Container
import io.kvision.html.P
import io.kvision.html.p

fun P.mute(): P {
    this.addCssClass("text-muted")
    return this
}