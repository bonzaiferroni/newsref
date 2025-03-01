package newsref.app.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import newsref.model.dto.ChapterPackDto

data class ChapterPack(
    val chapter: Chapter,
    val sources: ImmutableList<SourceBit>
)

fun ChapterPackDto.toModel() = ChapterPack(
    chapter = this.chapter.toChapter(),
    sources = this.sourceBits.map { it.toSourceBit() }.toImmutableList()
)