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

class EzForm<T>(
    name: String? = null,
    onSubmit: (suspend (T) -> Unit)? = null,
    block: EzForm<T>.() -> Unit,
) : VPanel(
    justify = JustifyContent.START, alignItems = AlignItems.STRETCH,
) {
    val flow = MutableStateFlow<T?>(null)

    init {
        gap = Layout.defaultGap
        if (name != null) {
            h3(name)
        }
        block?.invoke(this)
        button("Submit") {
            onClickLaunch {
                val value = flow.value ?: return@onClickLaunch
                onSubmit?.invoke(value)
            }
        }
    }
}

fun <T> Container.ezForm(
    name: String? = null,
    onSubmit: (suspend (T) -> Unit)? = null,
    block: EzForm<T>.() -> Unit,
): EzForm<T> {
    val form = EzForm(name, onSubmit, block)
    this.add(form)
    return form
}

fun <T> EzForm<T>.ezText(name: String, bindTo: (String, T?) -> T?, bindFrom: (T?) -> String?): Text {
    return text(label = name) {
        placeholder = name
    }
        .bindTo { flow.value = bindTo(it, flow.value) }
        .bindFrom { bindFrom(flow.value) ?: "" }
}