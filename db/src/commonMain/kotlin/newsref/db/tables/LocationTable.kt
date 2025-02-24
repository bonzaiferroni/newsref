package newsref.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object LocationTable : IntIdTable("location") {
    val name = text("name").uniqueIndex()
}