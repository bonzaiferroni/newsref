package newsref.db.tables

import kotlinx.datetime.LocalDateTime
import newsref.db.utils.toInstantUtc
import newsref.model.core.toUrl
import newsref.model.data.Feed
import newsref.model.data.LeadJob
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

internal object FeedTable : IntIdTable("feed") {
    val url = text("url")
    val selector = text("selector")
    val external = bool("external").default(false)
    val createdAt = datetime("created_at").defaultExpression(nowExpression)
}

val nowExpression = object : Expression<LocalDateTime>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) {
        queryBuilder.append("NOW()")
    }
}

class FeedRow(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, FeedRow>(FeedTable)

    var url by FeedTable.url
    var selector by FeedTable.selector
    var external by FeedTable.external
    var createdAt by FeedTable.createdAt
}

fun FeedRow.toData() = Feed(
    id = this.id.value,
    url = this.url.toUrl(),
    selector = this.selector,
    external = this.external,
    createdAt = this.createdAt.toInstantUtc()
)

fun ResultRow.toFeed() = Feed(
    id = this[FeedTable.id].value,
    url = this[FeedTable.url].toUrl(),
    selector = this[FeedTable.selector],
    external = this[FeedTable.external],
    createdAt = this[FeedTable.createdAt].toInstantUtc()
)

fun FeedRow.fromData(feed: Feed) {
    url = feed.url.toString()
    selector = feed.selector
    external = feed.external
}