package streetlight.web.ui.components

import io.kvision.core.Container
import io.kvision.core.Transition
import io.kvision.html.P
import io.kvision.html.p
import kotlinx.coroutines.flow.StateFlow

fun emoji(test: Boolean) = if (test) "💪" else "🙅"

fun <T> Container.validMsg(
    flow: StateFlow<T>,
    msg: String,
    isVisible: ((T) -> Boolean)? = null,
    isValid: (T) -> Boolean,
): P {
    val p = p() {
        transition = Transition("all", .3, "ease")
    }.mute()
    p.bindFrom(flow) {
        p.opacity = if (isVisible?.invoke(it) != false) 1.0 else 0.0
        val valid = isValid(it)
        "${emoji(valid)} $msg"
    }
    return p
}