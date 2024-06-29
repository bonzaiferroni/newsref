package com.example

import io.kvision.core.Container
import io.kvision.form.text.text
import io.kvision.html.*
import io.kvision.panel.vPanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.utils.px

fun Container.homeVis() {
    val count = ObservableValue(0)

    div {
        padding = 10.px
        vPanel(spacing = 5) {
            h1().bind(count) {
                +"World count = $it"
            }
            button("+") {
                onClick {
                    count.value++
                }
            }
            div("1")
            div("2")
            div("3")
            val text = text(label = "Enter something")
            div().bind(text) {
                +"You entered: $it"
            }
        }
    }
}