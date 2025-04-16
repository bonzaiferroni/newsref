package newsref.db.services

import klutch.db.Aspect
import newsref.db.tables.ChapterPersonTable
import newsref.db.tables.PersonTable
import newsref.model.data.ChapterPerson
import org.jetbrains.exposed.sql.ResultRow

internal object ChapterPersonAspect : Aspect<ChapterPersonAspect, ChapterPerson>(
    ChapterPersonTable.leftJoin(PersonTable),
    ResultRow::toChapterPerson
) {
    val personId = add(ChapterPersonTable.personId)
    val chapterId = add(ChapterPersonTable.chapterId)
    val mentions = add(ChapterPersonTable.mentions)
    val name = add(PersonTable.name)
    val identifiers = add(PersonTable.identifiers)
}

internal fun ResultRow.toChapterPerson() = ChapterPerson(
    personId = this[ChapterPersonTable.personId].value,
    chapterId = this[ChapterPersonTable.chapterId].value,
    name = this[PersonTable.name],
    identifiers = this[PersonTable.identifiers].toSet(),
    mentions = this[ChapterPersonTable.mentions],
)