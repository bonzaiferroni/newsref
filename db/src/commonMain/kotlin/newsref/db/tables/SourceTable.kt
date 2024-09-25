package newsref.db.tables

import newsref.model.data.Source
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object SourceTable: LongIdTable() {
    val url = text("url")
}

class SourceEntity(id: EntityID<Long>): LongEntity(id) {
    companion object: LongEntityClass<SourceEntity>(SourceTable)

    var url by SourceTable.url

    val articles by ArticleEntity via ArticleSourceTable
    val articleSources by ArticleSourceEntity referrersOn ArticleSourceTable
}

fun SourceEntity.toData() = Source(
    this.id.value,
    this.url,
)

fun SourceEntity.fromData(data: Source) {
    url = data.url
}