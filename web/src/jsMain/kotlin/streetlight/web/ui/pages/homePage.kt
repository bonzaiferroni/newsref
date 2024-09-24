package streetlight.web.ui.pages

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.Transition
import io.kvision.core.onClickLaunch
import io.kvision.form.text.text
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.p
import io.kvision.panel.FlexPanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.bindTo
import streetlight.web.Layout
import streetlight.web.core.PortalEvents
import streetlight.web.gap
import streetlight.web.ui.components.col
import streetlight.web.ui.components.row

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