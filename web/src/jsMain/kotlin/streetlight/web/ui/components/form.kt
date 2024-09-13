package streetlight.web.ui.components

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.JustifyContent
import io.kvision.form.text.Text
import io.kvision.form.text.text
import io.kvision.html.h3
import io.kvision.panel.FlexPanel
import streetlight.web.Layout
import streetlight.web.gap

class EzForm(
    name: String? = null,
    block: EzForm.() -> Unit,
) : FlexPanel(
    justify = JustifyContent.START, alignItems = AlignItems.STRETCH, direction = FlexDirection.COLUMN
) {
    init {
        gap = Layout.defaultGap
        if (name != null) {
            h3(name)
        }
        block.invoke(this)
    }
}

fun Container.ezForm(
    name: String? = null,
    block: EzForm.() -> Unit,
): EzForm {
    val form = EzForm(name, block)
    this.add(form)
    return form
}

fun Container.ezText(name: String): Text {
    return text(label = name) {
        placeholder = name
    }
}