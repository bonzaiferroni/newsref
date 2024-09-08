package streetlight.web.components

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.panel.HPanel
import io.kvision.panel.VPanel
import io.kvision.panel.hPanel
import io.kvision.panel.vPanel
import streetlight.web.Layout
import streetlight.web.Layout.spacing
import streetlight.web.gap

fun Container.rows(
    justify: JustifyContent = JustifyContent.SPACEBETWEEN,
    alignItems: AlignItems = AlignItems.START,
    useWrappers: Boolean = false,
    group: Boolean = false,
    init: (VPanel.() -> Unit)? = null,
): VPanel {
    return this.vPanel(justify = justify, alignItems = alignItems, useWrappers = useWrappers) {
        addCssClass("sl-rows")
        gap = if (group) Layout.halfGap else Layout.defaultGap
        init?.invoke(this)
    }
}

fun Container.row(
    justify: JustifyContent = JustifyContent.SPACEBETWEEN,
    alignItems: AlignItems = AlignItems.CENTER,
    useWrappers: Boolean = false,
    group: Boolean = false,
    init: (HPanel.() -> Unit)? = null,
): HPanel {
    return this.hPanel(justify = justify, alignItems = alignItems, useWrappers = useWrappers) {
        addCssClass("sl-row")
        gap = if (group) Layout.halfGap else Layout.defaultGap
        init?.invoke(this)
    }
}
