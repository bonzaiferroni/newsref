package newsref.db.tables

import newsref.db.model.Author
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SizedCollection

internal object AuthorTable: IntIdTable("author") {
    val name = text("name").index()
    val url = text("url").nullable()
    val bylines = array<String>("bylines").index()
}

internal class AuthorRow(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, AuthorRow>(AuthorTable)

    var name by AuthorTable.name
    var url by AuthorTable.url
    var bylines by AuthorTable.bylines

    var hosts by HostRow via HostAuthorTable
    var sources by SourceRow via SourceAuthorTable
}

internal object HostAuthorTable : CompositeIdTable("host_author") {
    val hostId = reference("host_id", HostTable, ReferenceOption.CASCADE).index()
    val authorId = reference("author_id", AuthorTable, ReferenceOption.CASCADE).index()
    override val primaryKey = PrimaryKey(hostId, authorId)
}

internal object SourceAuthorTable : CompositeIdTable("source_author") {
    val sourceId = reference("source_id", PageTable, ReferenceOption.CASCADE).index()
    val authorId = reference("author_id", AuthorTable, ReferenceOption.CASCADE).index()
    override val primaryKey = PrimaryKey(sourceId, authorId)
}

internal fun AuthorRow.toModel() = Author(
    id = this.id.value,
    name = this.name,
    url = this.url,
    bylines = this.bylines.toSet(),
)

internal fun AuthorRow.fromModel(author: Author, hostRow: HostRow, sourceRow: SourceRow) {
    name = author.name
    url = author.url
    hosts = SizedCollection(hostRow)
    sources = SizedCollection(sourceRow)
    bylines = author.bylines.toList()
}