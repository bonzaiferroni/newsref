package streetlight.web.components

import io.kvision.core.Widget
import io.kvision.core.onChange
import io.kvision.core.onClickLaunch
import io.kvision.core.onInput
import io.kvision.form.check.CheckBox
import io.kvision.form.text.Text
import io.kvision.html.Button
import io.kvision.html.P
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.JsonNull.content
import streetlight.web.subscribe

fun Text.bindTo(block: (String) -> Unit): Text {
    onInput { block(value ?: "") }
    return this
}

fun <T> Text.bindFrom(flow: StateFlow<T>, block: (T) -> String?): Text {
    value = block(flow.value) ?: ""
    flow.subscribe { value = block(it) ?: "" }
    return this
}

fun Text.bindFrom(flow: StateFlow<String>): Text {
    flow.subscribe { value = it }
    return this
}

fun CheckBox.bindTo(block: (Boolean) -> Unit): CheckBox {
    onChange { block(value) }
    return this
}

fun <T> CheckBox.bindFrom(flow: StateFlow<T>, block: (T) -> Boolean): CheckBox {
    flow.subscribe { value = block(it) }
    return this
}

fun <T> P.bindFrom(flow: StateFlow<T>, block: (T) -> String): P {
    flow.subscribe { content = block(it) }
    return this
}

fun Button.bindTo(block: () -> Unit): Button {
    onClick { block() }
    return this
}

fun Button.bindTo(block: suspend () -> Unit): Button {
    onClickLaunch { block() }
    return this
}


fun <W: Widget, F> W.bindWidgetFrom(flow: StateFlow<F>, block: W.(F) -> Unit): W {
    flow.subscribe { block(this, it) }
    return this
}