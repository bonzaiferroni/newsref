package newsref.db.tables

import newsref.model.core.parseUnchecked
import newsref.model.data.Feed
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object FeedTable : IntIdTable("feed") {
    val url = text("url")
    val selector = text("selector")
}

internal class FeedRow(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, FeedRow>(FeedTable)

    var url by FeedTable.url
    var selector by FeedTable.selector
}

internal fun FeedRow.toData() = Feed(
    id = this.id.value,
    url = this.url.parseUnchecked(),
    selector = this.selector,
)

internal fun FeedRow.fromData(feed: Feed) {
    url = feed.url.toString()
    selector = feed.selector
}