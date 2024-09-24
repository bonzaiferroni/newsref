package newsref.web.ui.components

import io.kvision.core.Container
import io.kvision.core.onClickLaunch
import io.kvision.html.Button
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import newsref.web.FaIcon

fun Button.bindTo(block: () -> Unit): Button {
    onClick { block() }
    return this
}

fun Button.bindToLaunch(block: suspend () -> Unit): Button {
    onClickLaunch { block() }
    return this
}

fun Container.iconButton(
    icon: FaIcon,
    style: ButtonStyle = ButtonStyle.PRIMARY,
    block: (Button.() -> Unit)? = null
): Button {
    return button("", icon = icon.css, style = style) {
        block?.invoke(this)
    }
}

fun Container.safetyButton(
    text: String = "",
    icon: FaIcon? = null,
    onClick: suspend () -> Unit
): Button {
    val button = button(text, icon = icon?.css, style = ButtonStyle.WARNING)
    button.onClickLaunch {
        if (button.style == ButtonStyle.WARNING) {
            button.style = ButtonStyle.DANGER
        } else {
            onClick()
        }
    }
    return button
}