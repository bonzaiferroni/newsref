package newsref.db.tables

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.json.Json
import newsref.db.utils.toLocalDateTimeUtc
import newsref.model.data.FeedSource
import newsref.model.data.FeedSourceLink
import newsref.model.data.FeedSourceScore
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

internal object FeedSourceTable : IntIdTable("feed_source") {
	val sourceId = long("source_id").uniqueIndex()
	val url = text("url")
	val hostCore = text("host_core")
	val headline = text("headline").nullable()
	val hostName = text("host_name").nullable()
	val hostLogo = text("host_logo").nullable()
	val citationCount = integer("citation_count")
	val wordCount = integer("word_count").nullable()
	val section = text("section").nullable()
	val thumbnail = text("thumbnail").nullable()
	val latestScore = integer("latest_score")
	val seenAt = datetime("seen_at")
	val publishedAt = datetime("published_at").nullable()
	val inLinks = json<Array<FeedSourceLink>>("in_links", Json.Default)
	val scores = json<Array<FeedSourceScore>>("scores", Json.Default)
	val authors = array<String>("authors").nullable()
}

internal class FeedSourceRow(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<FeedSourceRow>(FeedSourceTable)

	var sourceId by FeedSourceTable.sourceId
	var url by FeedSourceTable.url
	var hostCore by FeedSourceTable.hostCore
	var headline by FeedSourceTable.headline
	var hostName by FeedSourceTable.hostName
	var hostLogo by FeedSourceTable.hostLogo
	var citationCount by FeedSourceTable.citationCount
	var wordCount by FeedSourceTable.wordCount
	var section by FeedSourceTable.section
	var thumbnail by FeedSourceTable.thumbnail
	var latestScore by FeedSourceTable.latestScore
	var seenAt by FeedSourceTable.seenAt
	var publishedAt by FeedSourceTable.publishedAt
	var inLinks by FeedSourceTable.inLinks
	var scores by FeedSourceTable.scores
	var authors by FeedSourceTable.authors
}

internal fun FeedSourceRow.toData() = FeedSource(
	id = this.id.value,
	sourceId = this.sourceId,
	url = this.url,
	headline = this.headline,
	hostCore = this.hostCore,
	hostName = this.hostName,
	hostLogo = this.hostLogo,
	wordCount = this.wordCount,
	section = this.section,
	thumbnail = this.thumbnail,
	latestScore = this.latestScore,
	seenAt = this.seenAt.toInstant(TimeZone.UTC),
	publishedAt = this.publishedAt?.toInstant(TimeZone.UTC),
	inLinks = this.inLinks.toList(),
	scores = this.scores.toList(),
	authors = this.authors?.toList(),
)

internal fun FeedSourceRow.fromData(feedSource: FeedSource) {
	sourceId = feedSource.sourceId
	url = feedSource.url
	hostCore = feedSource.hostCore
	headline = feedSource.headline
	hostName = feedSource.hostName
	hostLogo = feedSource.hostLogo
	citationCount = feedSource.scores.size
	wordCount = feedSource.wordCount
	section = feedSource.section
	thumbnail = feedSource.thumbnail
	latestScore = feedSource.latestScore
	seenAt = feedSource.seenAt.toLocalDateTimeUtc()
	publishedAt = feedSource.publishedAt?.toLocalDateTimeUtc()
	inLinks = feedSource.inLinks.toTypedArray()
	scores = feedSource.scores.toTypedArray()
	authors = feedSource.authors?.toList()
}