package newsref.db.tables

import newsref.db.utils.toTrustedUrl
import newsref.db.utils.toUri
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object FeedTable : IntIdTable("feed") {
    val url = text("url")
    val selector = text("selector")
}

class FeedRow(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, FeedRow>(FeedTable)

    var url by FeedTable.url
    var selector by FeedTable.selector
}

fun FeedRow.toData() = newsref.model.data.Feed(
    id = this.id.value,
    url = this.url.toTrustedUrl(),
    selector = this.selector,
)

fun FeedRow.fromData(feed: newsref.model.data.Feed) {
    url = feed.url.toString()
    selector = feed.selector
}