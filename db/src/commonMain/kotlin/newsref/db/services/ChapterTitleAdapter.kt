package newsref.db.services

import newsref.db.model.Huddle
import newsref.db.tables.ChapterTable
import newsref.db.tables.HuddleAspect
import newsref.model.core.HuddleType
import newsref.model.data.HuddleKey
import newsref.model.data.HuddleOption
import org.jetbrains.exposed.sql.update

object ChapterTitleAdapter : HuddleAdapter(HuddleType.EditChapterTitle) {

    fun getKey(chapterId: Long) = HuddleKey(
        type = HuddleType.EditChapterTitle,
        chapterId = chapterId
    )

    override suspend fun readOptions(key: HuddleKey): List<HuddleOption> =
        HuddleAspect.readActiveOrNull(key)?.options?.takeIf { it.isNotEmpty() }
            ?: readCurrentValue(key)?.let { listOf(HuddleOption(null, it)) }
            ?: emptyList()

    override suspend fun readCurrentValue(key: HuddleKey): String? = ChapterTable.select(ChapterTable.title)
        .where { ChapterTable.id.eq(key.chapterId) }
        .first()[ChapterTable.title]

    override suspend fun readGuide(key: HuddleKey) = "[Placeholder for ChapterTitle Guide]"

    override suspend fun updateDatabase(consensus: String, huddle: Huddle) {
        ChapterTable.update({ ChapterTable.id.eq(huddle.chapterId) }) {
            it[title] = consensus
            it[titleHuddleId] = huddle.id
        }
    }
}