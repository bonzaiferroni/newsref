package streetlight.web.components

import io.kvision.core.BsBgColor
import io.kvision.core.ClassSetBuilder
import io.kvision.core.Container
import io.kvision.html.Div
import io.kvision.html.div
import io.kvision.navbar.Navbar
import io.kvision.navbar.NavbarColor
import io.kvision.panel.SimplePanel
import streetlight.web.Constants.defaultGap
import streetlight.web.Constants.defaultPad
import streetlight.web.Constants.halfPad

class Card(
    className: String? = null,
    bgColor: BsBgColor = BsBgColor.BODYTERTIARY,
    init: (Div.() -> Unit)? = null
) : SimplePanel(className) {

    var bgColor by refreshOnUpdate(bgColor)

    init {
        val div = div(className = "card-body") {
            padding = halfPad
        }
        init?.invoke(div)
    }

    override fun buildClassSet(classSetBuilder: ClassSetBuilder) {
        super.buildClassSet(classSetBuilder)
        classSetBuilder.add("card")
        classSetBuilder.add("border-0")
        classSetBuilder.add(bgColor.className)
    }
}

fun Container.card(
    className: String? = null,
    bgColor: BsBgColor = BsBgColor.BODYSECONDARY,
    init: (Div.() -> Unit)? = null
): Card {
    val card = Card(className, bgColor, init)
    this.add(card)
    return card
}