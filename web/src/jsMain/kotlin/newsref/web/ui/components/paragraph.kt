package newsref.web.ui.components

import io.kvision.html.P
import newsref.web.Css

fun P.mute(): P {
    this.addCssClass(Css.text_muted)
    this.addCssClass(Css.text_shadow_none)
    return this
}