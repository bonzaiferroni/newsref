package newsref.db.tables

import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

internal object AuthorTable: IntIdTable("author") {
    val name = text("name").index()
    val url = text("url").nullable()
    val bylines = array<String>("bylines").index()
}

internal object HostAuthorTable : CompositeIdTable("host_author") {
    val hostId = reference("host_id", HostTable, ReferenceOption.CASCADE).index()
    val authorId = reference("author_id", AuthorTable, ReferenceOption.CASCADE).index()
    override val primaryKey = PrimaryKey(hostId, authorId)
}

internal object PageAuthorTable : CompositeIdTable("page_author") {
    val pageId = reference("page_id", PageTable, ReferenceOption.CASCADE).index()
    val authorId = reference("author_id", AuthorTable, ReferenceOption.CASCADE).index()
    override val primaryKey = PrimaryKey(pageId, authorId)
}