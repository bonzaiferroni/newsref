package newsref.db.tables

import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*

object ChapterExclusionTable : LongIdTable("chapter_exclusion") {
    val chapterId = reference("chapter_id", ChapterTable, ReferenceOption.CASCADE).index()
    val pageId = reference("page_id", PageTable, ReferenceOption.CASCADE).index()

    init {
        uniqueIndex(chapterId, pageId)
    }
}