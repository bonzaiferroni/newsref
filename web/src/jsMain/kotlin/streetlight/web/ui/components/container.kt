package streetlight.web.ui.components

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.JustifyContent
import io.kvision.panel.FlexPanel
import io.kvision.panel.flexPanel
import streetlight.web.Layout
import streetlight.web.gap

fun Container.container(
    className: String? = null,
    justify: JustifyContent = JustifyContent.START,
    alignItems: AlignItems = AlignItems.STRETCH,
    useWrappers: Boolean = false,
    group: Boolean = false,
    direction: FlexDirection = FlexDirection.COLUMN,
    init: (FlexPanel.() -> Unit)? = null,
): FlexPanel {
    return this.flexPanel(
        className = className,
        justify = justify,
        alignItems = alignItems,
        useWrappers = useWrappers,
    ) {
        flexDirection = direction
        gap = if (group) Layout.halfGap else Layout.defaultGap
        init?.invoke(this)
    }
}

fun Container.row(
    className: String? = null,
    justify: JustifyContent = JustifyContent.START,
    alignItems: AlignItems = AlignItems.START,
    useWrappers: Boolean = false,
    group: Boolean = false,
    direction: FlexDirection = FlexDirection.ROW,
    init: (FlexPanel.() -> Unit)? = null,
): FlexPanel {
    return this.flexPanel(
        className = className,
        justify = justify,
        alignItems = alignItems,
        useWrappers = useWrappers,
    ) {
        flexDirection = direction
        gap = if (group) Layout.halfGap else Layout.defaultGap
        init?.invoke(this)
    }
}

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
        className = className,
        justify = justify,
        alignItems = alignItems,
        useWrappers = useWrappers
    ) {
        flexDirection = direction
        gap = if (group) Layout.halfGap else Layout.defaultGap
        init?.invoke(this)
    }
}
