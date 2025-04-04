package newsref.model.data

import kotlinx.serialization.*

@Serializable
data class ChapterPack(
    val chapter: Chapter,
    val pageBits: List<PageBit>
) {
    val imageUrl
        get() = pageBits.firstNotNullOfOrNull {
            if (it.contentType != ContentType.NewsArticle) return@firstNotNullOfOrNull null
            it.imageUrl
        }
}