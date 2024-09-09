package streetlight.web.components

import io.kvision.core.*
import io.kvision.form.text.Text
import io.kvision.form.text.text
import io.kvision.html.button
import io.kvision.html.h3
import io.kvision.panel.VPanel
import kotlinx.coroutines.flow.MutableStateFlow
import streetlight.web.Layout
import streetlight.web.gap

class EzForm(
    name: String? = null,
    block: EzForm.() -> Unit,
) : VPanel(
    justify = JustifyContent.START, alignItems = AlignItems.STRETCH,
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