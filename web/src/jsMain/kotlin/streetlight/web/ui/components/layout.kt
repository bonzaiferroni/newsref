package streetlight.web.ui.components

import io.kvision.core.*
import io.kvision.panel.FlexPanel
import io.kvision.panel.flexPanel
import streetlight.web.Css

fun Container.col(
    className: String? = null,
    justify: JustifyContent = JustifyContent.STRETCH,
    alignItems: AlignItems = AlignItems.START,
    useWrappers: Boolean = false,
    group: Boolean = false,
    direction: FlexDirection = FlexDirection.COLUMN,
    init: (FlexPanel.() -> Unit)? = null,
): FlexPanel {
    val tw = if (group) Css.col_group else Css.col_default
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
    val tw = if (group) Css.row_group else Css.row_default
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

fun Widget.grow(): Widget {
    this.addCssClass(Css.grow)
    return this
}

fun Widget.expand(): Widget {
    this.addCssClass(Css.expand)
    return this
}