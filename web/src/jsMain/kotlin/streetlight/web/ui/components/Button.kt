package streetlight.web.ui.components

import io.kvision.core.Container
import io.kvision.core.onClickLaunch
import io.kvision.html.Button
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import streetlight.web.FaIcon

fun Button.bindTo(block: () -> Unit): Button {
    onClick { block() }
    return this
}

fun Button.bindTo(block: suspend () -> Unit): Button {
    onClickLaunch { block() }
    return this
}

fun Container.iconButton(
    icon: FaIcon,
    style: ButtonStyle = ButtonStyle.PRIMARY,
    block: (Button.() -> Unit)? = null
): Button {
    return button("", icon = icon.value, style = style) {
        block?.invoke(this)
    }
}