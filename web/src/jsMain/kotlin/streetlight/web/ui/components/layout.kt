package streetlight.web.ui.components

import io.kvision.core.*
import io.kvision.panel.FlexPanel
import io.kvision.panel.flexPanel
import streetlight.web.Tailwind

fun Container.col(
    className: String? = null,
    justify: JustifyContent = JustifyContent.STRETCH,
    alignItems: AlignItems = AlignItems.START,
    useWrappers: Boolean = false,
    group: Boolean = false,
    direction: FlexDirection = FlexDirection.COLUMN,
    init: (FlexPanel.() -> Unit)? = null,
): FlexPanel {
    return this.flexPanel(
        className = "${Tailwind.col} ${className ?: ""}",
        justify = justify,
        alignItems = alignItems,
        useWrappers = useWrappers
    ) {
        flexDirection = direction
        init?.invoke(this)
    }
}

fun Container.row(
    className: String? = null,
    justify: JustifyContent = JustifyContent.SPACEBETWEEN,
    alignItems: AlignItems = AlignItems.START,
    useWrappers: Boolean = false,
    group: Boolean = false,
    direction: FlexDirection = FlexDirection.ROW,
    init: (FlexPanel.() -> Unit)? = null,
): FlexPanel {
    return this.flexPanel(
        className = "${Tailwind.row} ${className ?: ""}",
        justify = justify,
        alignItems = alignItems,
        useWrappers = useWrappers,
    ) {
        flexDirection = direction
        init?.invoke(this)
    }
}

fun Widget.flex1(): Widget {
    this.addCssClass(Tailwind.flex1)
    return this
}