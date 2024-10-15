package newsref.db.tables

import newsref.model.core.toUrl
import newsref.model.data.Feed
import newsref.model.data.LeadJob
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable

internal object FeedTable : IntIdTable("feed") {
    val url = text("url")
    val selector = text("selector")
}

class FeedRow(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, FeedRow>(FeedTable)

    var url by FeedTable.url
    var selector by FeedTable.selector
}

fun FeedRow.toData() = Feed(
    id = this.id.value,
    url = this.url.toUrl(),
    selector = this.selector,
)

fun FeedRow.fromData(feed: Feed) {
    url = feed.url.toString()
    selector = feed.selector
}