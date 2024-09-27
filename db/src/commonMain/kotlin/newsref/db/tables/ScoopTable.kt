package newsref.db.tables

import newsref.model.data.Scoop
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object ScoopTable : LongIdTable("scoop") {
    val url = text("url")
    val html = text("html")
}

class ScoopRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, ScoopRow>(ScoopTable)

    var url by ScoopTable.url
    var html by ScoopTable.html
}

fun ScoopRow.toData() = Scoop(
    id = this.id.value,
    url = this.url,
    html = this.html
)

fun ScoopRow.fromData(scoop: Scoop) {
    url = scoop.url
    html = scoop.html
}