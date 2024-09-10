package streetlight.web.ui.components

import io.kvision.core.AlignItems
import io.kvision.core.ClassSetBuilder
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.panel.VPanel

class Typography(
    justify: JustifyContent? = null, alignItems: AlignItems? = null, spacing: Int? = null,
    useWrappers: Boolean = false,
    className: String? = null, init: (VPanel.() -> Unit)? = null
) : VPanel(
    justify = justify, alignItems = alignItems, spacing = spacing, useWrappers = useWrappers,
    className = className, init = init
) {
    override fun buildClassSet(classSetBuilder: ClassSetBuilder) {
        super.buildClassSet(classSetBuilder)
        classSetBuilder.add("typography")
    }
}

fun Container.typography(
    justify: JustifyContent? = null, alignItems: AlignItems? = null, spacing: Int? = null,
    useWrappers: Boolean = false, className: String? = null, init: (VPanel.() -> Unit)? = null
): Typography {
    val typography = Typography(justify, alignItems, spacing, useWrappers, className, init)
    this.add(typography)
    return typography
}