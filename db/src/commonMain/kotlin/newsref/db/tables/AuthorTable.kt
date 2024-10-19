package newsref.db.tables

import newsref.model.data.Author
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SizedCollection

internal object AuthorTable: IntIdTable("author") {
    val name = text("name").nullable()
    val bylines = array<String>("bylines")
}

internal class AuthorRow(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, AuthorRow>(AuthorTable)

    var name by AuthorTable.name
    var bylines by AuthorTable.bylines

    var hosts by HostRow via HostAuthorTable
    var sources by SourceRow via SourceAuthorTable
}

internal object HostAuthorTable : CompositeIdTable("host_author") {
    val hostId = reference("host_id", HostTable)
    val authorId = reference("author_id", AuthorTable)
    override val primaryKey = PrimaryKey(hostId, authorId)
}

internal object SourceAuthorTable : CompositeIdTable("source_author") {
    val sourceId = reference("source_id", SourceTable)
    val authorId = reference("author_id", AuthorTable)
    override val primaryKey = PrimaryKey(sourceId, authorId)
}

internal fun AuthorRow.toData() = Author(
    id = this.id.value,
    name = this.name,
    bylines = this.bylines.toSet(),
)

internal fun AuthorRow.fromData(author: Author, hostRow: HostRow, sourceRow: SourceRow) {
    name = author.name
    hosts = SizedCollection(hostRow)
    sources = SizedCollection(sourceRow)
    bylines = author.bylines.toList()
}