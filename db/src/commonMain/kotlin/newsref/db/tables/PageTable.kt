package newsref.db.tables

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import klutch.db.Aspect
import newsref.db.model.DocumentType
import newsref.db.model.Page
import newsref.db.utils.toCheckedFromTrusted
import klutch.utils.toInstantUtc
import klutch.utils.toLocalDateTimeUtc
import newsref.model.data.ArticleType
import newsref.model.data.ContentType
import newsref.model.data.NewsSection
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import kotlin.time.Duration

internal object PageTable : LongIdTable("page") {
    // page properties
    val hostId = reference("host_id", HostTable, ReferenceOption.CASCADE).index()
    val locationId = reference("location_id", LocationTable, ReferenceOption.CASCADE).nullable().index()

    val url = text("url").uniqueIndex()
    val title = text("title").nullable()
    val contentType = enumeration<ContentType>("content_type").nullable()
    val score = integer("score").nullable()
    val feedPosition = integer("feed_position").nullable()
    val imageUrl = text("image_url").nullable()
    val thumbnail = text("thumbnail").nullable()
    val embed = text("embed").nullable()
    val cachedWordCount = integer("cached_word_count").nullable()
    val okResponse = bool("ok_response").default(false)

    val headline = text("headline").nullable()
    val alternativeHeadline = text("alternative_headline").nullable()
    val description = text("description").nullable()
    val cannonUrl = text("cannon_url").nullable()
    val metaSection = text("meta_section").nullable()
    val keywords = array<String>("keywords").nullable()
    val wordCount = integer("word_count").nullable()
    val isFree = bool("is_free").nullable()
    val language = text("language").nullable()
    val commentCount = integer("comment_count").nullable()
    val articleTypeHuddleId = reference("article_type_huddle_id", HuddleTable, ReferenceOption.SET_NULL).nullable()
    val summary = text("summary").nullable()
    val documentType = enumeration<DocumentType>("document_type").nullable()
    val section = enumeration<NewsSection>("section").nullable()
    val articleType = enumeration<ArticleType>("news_type").default(ArticleType.Unknown)

    val seenAt = datetime("seen_at").index()
    val accessedAt = datetime("accessed_at").nullable()
    val publishedAt = datetime("published_at").nullable().index()
    val modifiedAt = datetime("modified_at").nullable()
}

object PageAspect : Aspect<PageAspect, Page>(PageTable, ResultRow::toPage) {
    val hostId = add(PageTable.hostId)
    val url = add(PageTable.url)
    val title = add(PageTable.title)
    val type = add(PageTable.contentType)
    val score = add(PageTable.score)
    val feedPosition = add(PageTable.feedPosition)
    val imageUrl = add(PageTable.imageUrl)
    val thumbnail = add(PageTable.thumbnail)
    val embed = add(PageTable.embed)
    val contentCount = add(PageTable.cachedWordCount)
    val okResponse = add(PageTable.okResponse)
    val seenAt = add(PageTable.seenAt)
    val accessedAt = add(PageTable.accessedAt)
    val publishedAt = add(PageTable.publishedAt)
}

internal fun PageTable.existedAfter(instant: Instant) = instant.toLocalDateTimeUtc().let {
    Op.build { (publishedAt.isNull() and seenAt.greater(it)) or publishedAt.greater(it) }
}

internal fun PageTable.existedSince(duration: Duration) = existedAfter(Clock.System.now() - duration)

internal fun ResultRow.toPage() = Page(
    id = this[PageTable.id].value,
    hostId = this[PageTable.hostId].value,
    locationId = this[PageTable.locationId]?.value,

    url = this[PageTable.url].toCheckedFromTrusted(),
    title = this[PageTable.title],
    contentType = this[PageTable.contentType],
    score = this[PageTable.score],
    feedPosition = this[PageTable.feedPosition],
    thumbnail = this[PageTable.thumbnail],
    imageUrl = this[PageTable.imageUrl],
    embed = this[PageTable.embed],
    cachedWordCount = this[PageTable.cachedWordCount],
    okResponse = this[PageTable.okResponse],

    headline = this[PageTable.headline],
    alternativeHeadline = this[PageTable.alternativeHeadline],
    description = this[PageTable.description],
    cannonUrl = this[PageTable.cannonUrl],
    metaSection = this[PageTable.metaSection],
    keywords = this[PageTable.keywords],
    wordCount = this[PageTable.wordCount],
    isFree = this[PageTable.isFree],
    language = this[PageTable.language],
    commentCount = this[PageTable.commentCount],
    summary = this[PageTable.summary],
    documentType = this[PageTable.documentType],
    section = this[PageTable.section],
    articleType = this[PageTable.articleType],

    seenAt = this[PageTable.seenAt].toInstantUtc(),
    accessedAt = this[PageTable.accessedAt]?.toInstantUtc(),
    publishedAt = this[PageTable.publishedAt]?.toInstantUtc(),
    modifiedAt = this[PageTable.modifiedAt]?.toInstantUtc()
)
