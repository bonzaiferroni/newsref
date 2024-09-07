package streetlight.web.pages

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.core.onClickLaunch
import io.kvision.form.text.text
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.panel.VPanel
import io.kvision.panel.hPanel
import io.kvision.panel.vPanel
import io.kvision.state.ObservableValue
import io.kvision.state.bindTo
import streetlight.model.Area
import streetlight.web.Layout
import streetlight.web.components.row
import streetlight.web.components.rows
import streetlight.web.core.PortalEvents
import streetlight.web.gap
import streetlight.web.io.stores.AreaStore

fun Container.aboutPage(): PortalEvents? {
    val store = AreaStore()
    lateinit var panel: VPanel
    val areaName = ObservableValue("")

    rows {
        button("get").onClickLaunch {
            panel.refreshAreas(store)
        }
        row() {
            gap = Layout.defaultGap
            text() {
                placeholder = "area"
            }.bindTo(areaName)
            val createButton = button("create")
            createButton.onClickLaunch {
                val id = store.create(Area(name = areaName.value))
                createButton.text = "created $id"
                panel.refreshAreas(store)
            }
        }
        panel = rows()
    }

    return null
}

suspend fun Container.refreshAreas(store: AreaStore) {

    this.removeAll()
    val areas = store.getAll()
    suspend fun refresh() { refreshAreas(store) }
    areas.forEach { area ->
        val areaName = ObservableValue(area.name)
        row() {
            text().bindTo(areaName)
            button("", icon = "fas fa-trash", style = ButtonStyle.DANGER).onClickLaunch {
                store.delete(area.id)
                refresh()
            }
            button("", icon = "fas fa-refresh", style = ButtonStyle.PRIMARY).onClickLaunch {
                console.log(areaName.value)
                store.update(area.copy(name = areaName.value))
            }
        }
    }
}