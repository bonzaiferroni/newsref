package newsref.db.tables

import newsref.db.core.toUrl
import newsref.db.model.Scoop
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

internal object ScoopTable : LongIdTable("scoop") {
    val url = text("uri")
    val html = text("html")
}

class ScoopRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : EntityClass<Long, ScoopRow>(ScoopTable)

    var url by ScoopTable.url
    var html by ScoopTable.html
}

fun ScoopRow.toModel() = Scoop(
    id = this.id.value,
    url = this.url.toUrl(),
    html = this.html
)

fun ScoopRow.fromModel(scoop: Scoop) {
    url = scoop.url.toString()
    html = scoop.html
}