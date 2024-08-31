package streetlight.web

import io.kvision.core.Container
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.html.h4
import io.kvision.panel.vPanel
import io.kvision.table.TableType
import io.kvision.table.cell
import io.kvision.table.row
import io.kvision.table.table
import io.kvision.utils.px

fun Container.penguin() {
    vPanel {
        div("hey y'all hey hey that is so cool")
        div("peanut butter jelly time") {
            fontSize = 32.px
            fontFamily = "Times New Roman"
        }
        h4("Header")
        button("Button")
        table(
            listOf("Column 1", "Column 2", "Column3"),
            setOf(TableType.BORDERED)
        ) {
            row {
                cell("Row 1 Cell 1")
                cell("Row 1 Cell 2")
                cell("Row 1 Cell 3")
            }
            row {
                cell("Row 2 Cell 1")
                cell("Row 2 Cell 2")
                cell("Row 2 Cell 3")
            }
        }
    }
}