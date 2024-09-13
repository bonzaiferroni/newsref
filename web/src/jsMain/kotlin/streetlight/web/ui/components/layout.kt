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
    val tw = if (group) Tailwind.colGroup else Tailwind.colDefault
    return this.flexPanel(
        className = className?.let { "$tw $className" } ?: tw,
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
    justify: JustifyContent = JustifyContent.FLEXSTART,
    alignItems: AlignItems = AlignItems.START,
    useWrappers: Boolean = false,
    group: Boolean = false,
    direction: FlexDirection = FlexDirection.ROW,
    init: (FlexPanel.() -> Unit)? = null,
): FlexPanel {
    val tw = if (group) Tailwind.rowGroup else Tailwind.rowDefault
    return this.flexPanel(
        className = className?.let { "$tw $className" } ?: tw,
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