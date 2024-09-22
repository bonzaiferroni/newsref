package streetlight.web.ui.components

import io.kvision.core.Container
import io.kvision.core.Transition
import io.kvision.html.*
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue

fun Container.menuButton(
    text: String, icon: String? = null, options: List<MenuButtonOption> = emptyList(),
    style: ButtonStyle = ButtonStyle.PRIMARY, type: ButtonType = ButtonType.BUTTON,
    disabled: Boolean = false, separator: String? = null, labelFirst: Boolean = true, className: String? = null,
    init: (MenuButton.() -> Unit)? = null
): MenuButton {
    val button = MenuButton(text = text, icon = icon, options = options, style = style, type = type,
        disabled = disabled, separator = separator, labelFirst = labelFirst, className = className, init = init)
    this.add(button)
    return button
}

class MenuButton(
    text: String, icon: String? = null, var options: List<MenuButtonOption> = emptyList(),
    style: ButtonStyle = ButtonStyle.PRIMARY, type: ButtonType = ButtonType.BUTTON,
    disabled: Boolean = false, separator: String? = null, labelFirst: Boolean = true, className: String? = null,
    init: (MenuButton.() -> Unit)? = null
) : SimplePanel(className) {

    var innerButton: Button = Button(text = text, icon = icon, style = style, type = type, disabled = disabled,
        separator = separator, labelFirst = labelFirst, className = className)

    val isVisible = ObservableValue(false)

    init {
        val box = div(className = "relative")
        init?.invoke(this)

        box.add(innerButton)
        innerButton.onClick { isVisible.value = !isVisible.value }

        box.div(className = "origin-top-right absolute right-0 mt-2 rounded-md shadow-lg bg-white") {
            transition = Transition("opacity", 0.3, "ease-in-out")
            div(className = "py-1") {
                val linkClass = "block px-4 py-2 text-gray-700 hover:bg-gray-200 cursor-pointer"
                options.forEach { option ->
                    link(option.text, option.href, className = linkClass) {
                        transition = Transition("background-color", 0.3, "ease-in-out")
                    }.onClick {
                        option.onClick?.invoke()
                        isVisible.value = false
                    }
                }
            }
            isVisible.subscribe {
                if (it) {
                    opacity = 1.0
                    removeCssClass("pointer-events-none")
                } else {
                    opacity = 0.0
                    addCssClass("pointer-events-none")
                }
            }
        }
    }
}

data class MenuButtonOption(
    val text: String,
    val href: String? = null,
    val onClick: (() -> Unit)? = null
)
