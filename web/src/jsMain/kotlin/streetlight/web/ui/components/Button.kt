package streetlight.web.ui.components

import io.kvision.core.Container
import io.kvision.core.onClick
import io.kvision.core.onClickLaunch
import io.kvision.html.Button
import io.kvision.html.ButtonStyle
import io.kvision.html.ButtonType
import io.kvision.html.button

fun Button.bindTo(block: () -> Unit): Button {
    onClick { block() }
    return this
}

fun Button.bindTo(block: suspend () -> Unit): Button {
    onClickLaunch { block() }
    return this
}

fun Container.buttonLaunch(
    label: String,
    style: ButtonStyle = ButtonStyle.PRIMARY,
    block: suspend () -> Unit
): Button {
    return button(label, style = style) { onClickLaunch { block() } }
}

fun Container.button(
    label: String, style:
    ButtonStyle = ButtonStyle.PRIMARY,
    block: () -> Unit
): Button {
    return button(label, style = style) { onClickLaunch { block() } }
}