package newsref.db.tables

import newsref.db.tables.AuthorTable.bylines
import newsref.model.data.Author
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SizedCollection

internal object AuthorTable: IntIdTable("author") {
    val bylines = array<String>("bylines")
}

internal class AuthorRow(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, AuthorRow>(AuthorTable)

    var bylines by AuthorTable.bylines

    var outlets by OutletRow via OutletAuthorTable
}

internal object OutletAuthorTable : CompositeIdTable("outlet_author") {
    val outletId = reference("outlet_id", OutletTable)
    val authorId = reference("author_id", AuthorTable)
    override val primaryKey = PrimaryKey(outletId, authorId)
}

internal fun AuthorRow.toData() = Author(
    id = this.id.value,
    bylines = this.bylines.toSet(),
)

internal fun AuthorRow.newFromData(author: Author, outletRow: OutletRow) {
    outlets = SizedCollection(outletRow)
    bylines = author.bylines.toList()
}