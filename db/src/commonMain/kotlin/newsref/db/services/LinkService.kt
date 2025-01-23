package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.AuthorTable
import newsref.db.tables.LeadTable
import newsref.db.tables.LinkTable
import newsref.db.tables.SourceAuthorTable
import newsref.db.tables.linkInfoColumns
import newsref.db.tables.linkInfoJoins
import newsref.db.tables.toLink
import newsref.db.tables.toLinkInfo
import newsref.model.dto.LinkCollection
import newsref.model.dto.LinkInfo
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and

class LinkService() : DbService() {
    suspend fun readOutboundLinks(sourceId: Long) = dbQuery {
        linkInfoJoins.select(linkInfoColumns)
            .where { LinkTable.sourceId eq sourceId }
            .map { it.toLinkInfo() }
    }

    suspend fun readInboundLinks(sourceId: Long) = dbQuery {
        linkInfoJoins.select(linkInfoColumns)
            .where { LeadTable.sourceId eq sourceId and LinkTable.id.isNotNull() }
            .map { it.toLinkInfo() }
    }
}

internal fun LinkTable.readLinkInfos(block: SqlExpressionBuilder.() -> Op<Boolean>): List<LinkInfo> {
    return linkInfoJoins.select(linkInfoColumns)
        .where(block)
        .map { it.toLinkInfo() }
}

internal fun LinkTable.readLinkCollections(block: SqlExpressionBuilder.() -> Op<Boolean>): List<LinkCollection> {
    val linkInfos = this.readLinkInfos(block)
    return linkInfos.map { linkInfo ->
        val authors = AuthorTable.getAuthors { SourceAuthorTable.sourceId.eq(linkInfo.originId) }
            .takeIf { it.isNotEmpty() }
        val snippet = linkInfo.context?.findContainingSentence(linkInfo.urlText)
        LinkCollection(info = linkInfo.copy(context = snippet), authors = authors)
    }
}