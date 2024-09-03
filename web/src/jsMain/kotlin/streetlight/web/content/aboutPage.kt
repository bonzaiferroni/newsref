package streetlight.web.content

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.core.onClickLaunch
import io.kvision.form.text.text
import io.kvision.html.button
import io.kvision.html.p
import io.kvision.panel.VPanel
import io.kvision.panel.hPanel
import io.kvision.panel.vPanel
import io.kvision.state.ObservableValue
import io.kvision.state.bindTo
import kotlinx.coroutines.asDeferred
import streetlight.model.Area
import streetlight.web.io.AreaStore

fun Container.aboutPage() {
    val store = AreaStore()
    val panel = VPanel()
    val areaName = ObservableValue("")

    vPanel(alignItems = AlignItems.START, spacing = 10) {
        button("get").onClickLaunch {
            panel.removeAll()
            val areas = store.getAll().asDeferred().await()
            areas.forEach {
                panel.add(p(it.name))
            }
        }
        hPanel(justify = JustifyContent.CENTER, spacing = 10) {
            text() {
                placeholder = "Area name"
            }.bindTo(areaName)
            val createButton = button("create")
            createButton.onClickLaunch {
                val id = store.create(Area(name = areaName.value))
                createButton.text = "created $id"
            }
        }
    }

    add(panel)
}