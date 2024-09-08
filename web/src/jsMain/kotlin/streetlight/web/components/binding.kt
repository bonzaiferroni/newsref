package streetlight.web.components

import io.kvision.core.Widget
import io.kvision.core.onClickLaunch
import io.kvision.core.onInput
import io.kvision.form.text.Text
import io.kvision.html.Button
import io.kvision.html.P
import kotlinx.coroutines.flow.StateFlow
import streetlight.web.subscribe

fun Text.bindTo(block: (String) -> Unit): Text {
    onInput { block(value ?: "") }
    return this
}

fun Text.bindFrom(block: () -> String): Text {
    value = block()
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