package newsref.app.model

import kotlinx.collections.immutable.*
import kotlinx.serialization.*
import newsref.model.core.*
import newsref.model.dto.*

@Serializable
data class ChapterPack(
    val chapter: Chapter,
    val sources: ImmutableList<SourceBit>
) {
    val imageUrl
        get() = sources.firstNotNullOfOrNull {
            if (it.pageType != PageType.NEWS_ARTICLE) return@firstNotNullOfOrNull null
            it.imageUrl
        }
}

fun ChapterPackDto.toModel() = ChapterPack(
    chapter = this.chapter.toChapter(),
    sources = this.sourceBits.map { it.toSourceBit() }.toImmutableList()
)