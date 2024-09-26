package newsref.db.tables

import newsref.model.data.Author
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SizedCollection

object AuthorTable: IntIdTable("author") {
    val byLines = array<String>("by_lines")
}

class AuthorRow(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, AuthorRow>(AuthorTable)

    var byLines by AuthorTable.byLines

    var outlets by OutletRow via OutletAuthorTable
}

object OutletAuthorTable : CompositeIdTable("outlet_author") {
    val outletId = reference("outlet_id", OutletTable)
    val authorId = reference("author_id", AuthorTable)
    override val primaryKey = PrimaryKey(outletId, authorId)
}

fun AuthorRow.toData() = Author(
    id = this.id.value,
    byLines = this.byLines.toSet(),
)

fun AuthorRow.fromData(author: Author, outletRow: OutletRow) {
    outlets = SizedCollection(outletRow)
    byLines = author.byLines.toList()
}