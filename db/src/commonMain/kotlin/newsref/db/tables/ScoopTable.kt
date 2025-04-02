package newsref.db.tables

import newsref.db.core.toUrl
import newsref.db.model.Scoop
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow

internal object ScoopTable : LongIdTable("scoop") {
    val url = text("uri")
    val html = text("html")
}

internal fun ResultRow.toScoop() = Scoop(
    id = this[ScoopTable.id].value,
    url = this[ScoopTable.url].toUrl(),
    html = this[ScoopTable.html],
)

//fun ScoopRow.fromModel(scoop: Scoop) {
//    url = scoop.url.toString()
//    html = scoop.html
//}