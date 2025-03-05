package newsref.db.tables

import kotlinx.datetime.Instant
import newsref.db.model.Source
import newsref.db.utils.*
import newsref.model.core.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

internal object PageTable : LongIdTable("source") {
    val hostId = reference("host_id", HostTable, ReferenceOption.CASCADE).index()
    val noteId = reference("note_id", NoteTable, ReferenceOption.SET_NULL).nullable().index()
    val url = text("url").uniqueIndex()
    val title = text("title").nullable()
    val type = enumeration<PageType>("source_type").nullable()
    val score = integer("score").nullable()
    val feedPosition = integer("feed_position").nullable()
    val imageUrl = text("image_url").nullable()
    val thumbnail = text("thumbnail").nullable()
    val embed = text("embed").nullable()
    val contentCount = integer("content_count").nullable()
    val okResponse = bool("ok_response").default(false)
    val seenAt = datetime("seen_at").index()
    val accessedAt = datetime("accessed_at").nullable()
    val publishedAt = datetime("published_at").nullable().index()
}
