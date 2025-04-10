package newsref.db.tables

import newsref.db.model.Person
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object PersonTable : IntIdTable("person") {
    val name = text("name")
    val identifiers = array<String>("identifiers")
}

object PagePersonTable : Table("page_person") {
    val pageId = reference("page_id", PageTable, ReferenceOption.CASCADE).index()
    val personId = reference("person_id", PersonTable, ReferenceOption.CASCADE).index()
    override val primaryKey = PrimaryKey(pageId, personId)
}

object ChapterPersonTable : Table("chapter_person") {
    val chapterId = reference("chapter_id", ChapterTable, ReferenceOption.CASCADE).index()
    val personId = reference("person_id", PersonTable, ReferenceOption.CASCADE).index()
    override val primaryKey = PrimaryKey(chapterId, personId)

    val mentions = integer("mentions")
}

fun ResultRow.toPerson() = Person(
    this[PersonTable.id].value,
    this[PersonTable.name],
    this[PersonTable.identifiers].toSet()
)