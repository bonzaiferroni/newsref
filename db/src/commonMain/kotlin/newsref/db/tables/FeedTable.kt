package newsref.db.tables

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object FeedTable : IntIdTable("feed") {
    val url = text("url")
    val leadSelector = text("lead_selector")
}

class FeedRow(id: EntityID<Int>): IntEntity(id) {
    companion object : EntityClass<Int, FeedRow>(FeedTable)

    var url by FeedTable.url
    var leadSelector by FeedTable.leadSelector
}

fun FeedRow.toData() = newsref.model.data.Feed(
    id = this.id.value,
    url = this.url,
    leadSelector = this.leadSelector,
)

fun FeedRow.fromData(feed: newsref.model.data.Feed) {
    url = feed.url
    leadSelector = feed.leadSelector
}