package streetlight.web.ui.components

import io.kvision.core.onClickLaunch
import io.kvision.html.Button

fun Button.bindTo(block: () -> Unit): Button {
    onClick { block() }
    return this
}

fun Button.bindTo(block: suspend () -> Unit): Button {
    onClickLaunch { block() }
    return this
}