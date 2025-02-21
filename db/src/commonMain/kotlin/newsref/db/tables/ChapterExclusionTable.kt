package newsref.db.tables

import newsref.db.core.*
import newsref.db.utils.*
import newsref.db.model.*
import newsref.model.dto.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.statements.InsertStatement

object ChapterExclusionTable : LongIdTable("chapter_exclusion") {
    val chapterId = reference("chapter_id", ChapterTable, ReferenceOption.CASCADE).index()
    val sourceId = reference("source_id", SourceTable, ReferenceOption.CASCADE).index()

    init {
        uniqueIndex(chapterId, sourceId)
    }
}