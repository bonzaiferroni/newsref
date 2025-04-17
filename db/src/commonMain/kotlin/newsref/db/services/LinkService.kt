package newsref.db.services

import klutch.db.DbService
import newsref.db.tables.AuthorTable
import newsref.db.tables.LeadTable
import newsref.db.tables.LinkTable
import newsref.db.tables.PageAuthorTable
import newsref.db.tables.linkInfoColumns
import newsref.db.tables.linkInfoJoins
import newsref.db.tables.toLinkInfo
import newsref.model.data.LinkCollection
import newsref.model.data.LinkInfo
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and

class LinkService() : DbService() {
    suspend fun readOutboundLinks(pageId: Long) = dbQuery {
        linkInfoJoins.select(linkInfoColumns)
            .where { LinkTable.pageId eq pageId }
            .map { it.toLinkInfo() }
    }

    suspend fun readInboundLinks(pageId: Long) = dbQuery {
        linkInfoJoins.select(linkInfoColumns)
            .where { LeadTable.pageId eq pageId and LinkTable.id.isNotNull() }
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
        val authors = AuthorTable.getAuthors { PageAuthorTable.pageId.eq(linkInfo.originId) }
            .takeIf { it.isNotEmpty() }
        val snippet = linkInfo.context?.findContainingSentence(linkInfo.urlText)
        LinkCollection(info = linkInfo.copy(context = snippet), authors = authors)
    }
}