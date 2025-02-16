package newsref.model.dto

import kotlinx.collections.immutable.ImmutableList
import newsref.model.data.*

data class ChapterDto(
    val chapter: Chapter,
    val sources: ImmutableList<Source>
)