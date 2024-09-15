package streetlight.web.ui.components

import io.kvision.html.P
import streetlight.web.Css

fun P.mute(): P {
    this.addCssClass(Css.text_muted)
    this.addCssClass(Css.text_shadow_none)
    return this
}