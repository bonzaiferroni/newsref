package newsref.web.ui.pages

import io.kvision.core.Container
import io.kvision.core.Transition
import io.kvision.form.text.text
import io.kvision.html.button
import io.kvision.html.p
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.bindTo
import newsref.web.core.PortalEvents
import newsref.web.ui.components.col

fun Container.homePage(): PortalEvents? {
    val message = ObservableValue("hello, world!")

    col {
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

    return null
}