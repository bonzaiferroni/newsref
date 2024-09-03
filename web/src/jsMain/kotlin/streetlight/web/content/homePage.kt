package streetlight.web.content

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.Transition
import io.kvision.form.text.text
import io.kvision.html.*
import io.kvision.panel.vPanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.bindTo

fun Container.homePage() {
    val message = ObservableValue("hello, world!")

    vPanel(spacing = 10, alignItems = AlignItems.START) {
        val p = p {
            transition = Transition("all", .3, "ease")
        }
        p.bind(message) {
            +it
        }
        button("show") {
            onClick {
                if (p.opacity != 0.0) {
                    p.opacity = 0.0
                } else {
                    p.opacity = 1.0
                }
            }
        }
        text() {
            placeholder = "Enter your message"
        }.bindTo(message)
    }
}